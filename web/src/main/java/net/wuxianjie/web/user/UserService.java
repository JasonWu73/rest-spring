package net.wuxianjie.web.user;

import cn.hutool.core.bean.BeanUtil;
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

import java.util.List;
import java.util.Optional;

/**
 * 用户管理业务逻辑实现类。
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
  public PagingResult<UserItemDto> getUsers(PagingQuery paging, GetUserQuery query) {
    List<UserItemDto> users = userMapper.findByUsernameLikeAndEnabledOrderByModifyTimeDesc(paging, query);

    int total = userMapper.countByUsernameLikeAndEnabled(query);

    return new PagingResult<>(paging, total, users);
  }

  /**
   * 新增用户。
   *
   * @param query 需要保存的用户数据
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveUser(SaveOrUpdateUserQuery query) {
    String username = query.getUsername();
    boolean isExisted = userMapper.existsByUsername(username);

    if (isExisted) {
      throw new ConflictException("用户名 [" + username + "] 已存在");
    }

    User toSave = buildUserToSave(query);

    userMapper.save(toSave);
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
  public void deleteUser(DelUserQuery query) {
    int userId = query.getUserId();
    User toDel = getUserFromDbMustBeExists(userId);

    userMapper.deleteByUserId(userId);

    populateQueryForOperationLog(toDel, query);
  }

  private User buildUserToSave(SaveOrUpdateUserQuery query) {
    User toSave = new User();

    BeanUtil.copyProperties(query, toSave, "enabled", "password");

    YesOrNo enabled = YesOrNo.resolve(query.getEnabled()).orElseThrow();
    toSave.setEnabled(enabled);

    String hashedPassword = passwordEncoder.encode(query.getPassword());
    toSave.setHashedPassword(hashedPassword);

    return toSave;
  }

  private User getUserFromDbMustBeExists(int userId) {
    return Optional.ofNullable(userMapper.findByUserId(userId))
      .orElseThrow(() -> new NotFoundException("用户不存在 [id=" + userId + "]"));
  }

  private void populateQueryForOperationLog(User user, DelUserQuery query) {
    query.setUsername(user.getUsername());
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
    if (newRoles != null && !StrUtils.equalsIgnoreBlank(newRoles, toUpdate.getRoles())) {
      needsUpdate = true;
      toUpdate.setRoles(newRoles);
    } else {
      toUpdate.setRoles(null);
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
}
