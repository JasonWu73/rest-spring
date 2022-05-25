package net.wuxianjie.web.user;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.exception.ConflictException;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.util.StrUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户管理的业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  /**
   * 获取用户列表。
   *
   * @param paging 分页参数
   * @param query  请求参数
   * @return 用户列表
   */
  public PagingResult<UserDto> getUsers(PagingQuery paging, GetUserQuery query) {
    List<UserDto> users = userMapper.findByUsernameLikeAndEnabledOrderByModifyTimeDesc(paging, query);
    int total = userMapper.countByUsernameLikeAndEnabled(query);
    return new PagingResult<>(paging, total, users);
  }

  /**
   * 新增用户。
   *
   * @param query 需要保存的用户数据
   * @return 新增成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> saveUser(SaveUserQuery query) {
    // 用户名唯一性校验
    String username = query.getUsername();
    boolean isExisted = userMapper.existsByUsername(username);
    if (isExisted) throw new ConflictException("已存在用户名 " + username);

    // 用户入库
    User toSave = new User();
    toSave.setEnabled(YesOrNo.resolve(query.getEnabled()).orElseThrow());
    toSave.setUsername(query.getUsername());
    toSave.setHashedPassword(passwordEncoder.encode(query.getPassword()));
    toSave.setMenus(query.getMenus());

    userMapper.save(toSave);

    return new HashMap<>() {{
      put("msg", "用户（" + username + "）新增成功");
    }};
  }

  /**
   * 修改用户。
   * <p>
   * 注意：此处为重置密码，即无需验证旧密码。
   * </p>
   *
   * @param query 需要更新的用户数据
   */
  @Transactional(rollbackFor = Exception.class)
  public void updateUser(SaveOrUpdateUserQuery query) {
    User toUpdate = getUserFromDbMustBeExists(query.getUserId());

    boolean needsUpdate = needsUpdate(toUpdate, query);

    if (!needsUpdate) {
      return;
    }

    userMapper.update(toUpdate);
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 新旧密码数据
   */
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(PasswordQuery query) {
    User toUpdate = getUserFromDbMustBeExists(query.getUserId());

    boolean isMatched = passwordEncoder.matches(query.getOldPassword(), toUpdate.getHashedPassword());

    if (!isMatched) {
      throw new BadRequestException("旧密码错误");
    }

    String hashedPassword = passwordEncoder.encode(query.getNewPassword());
    toUpdate.setHashedPassword(hashedPassword);

    userMapper.update(toUpdate);
  }

  /**
   * 删除用户。
   *
   * @param query 需要删除的用户
   */
  @Transactional(rollbackFor = Exception.class)
  public void deleteUser(LogOfDelUserQuery query) {
    int userId = query.getUserId();
    User toDel = getUserFromDbMustBeExists(userId);

    userMapper.deleteByUserId(userId);

    populateQueryForOperationLog(toDel, query);
  }

  private User getUserFromDbMustBeExists(int userId) {
    return Optional.ofNullable(userMapper.findByUserId(userId))
      .orElseThrow(() -> new NotFoundException("用户不存在 [id=" + userId + "]"));
  }

  private boolean needsUpdate(User toUpdate, SaveOrUpdateUserQuery query) {
    boolean needsUpdate = false;

    String newPassword = query.getPassword();
    if (newPassword != null && !passwordEncoder.matches(newPassword, toUpdate.getHashedPassword())) {
      needsUpdate = true;
      toUpdate.setHashedPassword(passwordEncoder.encode(newPassword));
    } else {
      toUpdate.setHashedPassword(null);
    }

    String newRoles = query.getRoles();
    if (newRoles != null && !StrUtils.equalsIgnoreBlank(newRoles, toUpdate.getMenus())) {
      needsUpdate = true;
      toUpdate.setMenus(newRoles);
    } else {
      toUpdate.setMenus(null);
    }

    Optional<YesOrNo> newEnabledOpt = YesOrNo.resolve(query.getEnabled());
    if (newEnabledOpt.isPresent() && newEnabledOpt.get() != toUpdate.getEnabled()) {
      needsUpdate = true;
      toUpdate.setEnabled(newEnabledOpt.get());
    } else {
      toUpdate.setEnabled(null);
    }

    return needsUpdate;
  }

  private void populateQueryForOperationLog(User user, LogOfDelUserQuery query) {
    query.setUsername(user.getUsername());
  }
}
