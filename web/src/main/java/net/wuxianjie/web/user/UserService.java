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
  private final ComUserService comUserService;

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
    // 校验并去重菜单编号字符串
    comUserService.toDeduplicatedCommaSeparatedMenus(query.getMenus()).ifPresent(query::setMenus);

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
   *
   * @param query 需要更新的用户数据
   * @return 修改成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> updateUser(UpdateUserQuery query) {
    // 校验并去重菜单编号字符串
    comUserService.toDeduplicatedCommaSeparatedMenus(query.getMenus()).ifPresent(query::setMenus);

    // 判断用户是否存在
    User oldUser = getUserFromDbMustBeExists(query.getUserId());
    String username = oldUser.getUsername();

    // 判断是否需要更新
    Optional<User> toUpdate = getUserToUpdate(oldUser, query);
    if (toUpdate.isEmpty()) return new HashMap<>() {{
      put("msg", "用户（" + username + "）无需修改");
    }};

    // 更新数据
    userMapper.update(toUpdate.get());

    return new HashMap<>() {{
      put("msg", "用户（" + username + "）修改成功");
    }};
  }

  /**
   * 重置用户密码。
   *
   * @param query  需要更新的用户数据
   * @return 密码重置成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> updateUserPwd(ResetUserPwdQuery query) {
    // 判断用户是否存在
    User oldUser = getUserFromDbMustBeExists(query.getUserId());
    String username = oldUser.getUsername();

    // 判断是否需要更新
    String newRawPassword = query.getPassword();
    boolean isMatched = passwordEncoder.matches(newRawPassword, oldUser.getHashedPassword());
    if (isMatched) return new HashMap<>() {{
      put("msg", "用户（" + username + "）密码无需重置");
    }};

    // 更新数据
    User toUpdate = new User();
    toUpdate.setUserId(oldUser.getUserId());
    toUpdate.setHashedPassword(passwordEncoder.encode(newRawPassword));
    userMapper.update(toUpdate);

    return new HashMap<>() {{
      put("msg", "用户（" + username + "）密码重置成功");
    }};
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 新旧密码数据
   * @return 密码修改成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> updateUserPwd(UpdateUserPwdQuery query) {
    // 判断用户是否存在
    User oldUser = getUserFromDbMustBeExists(query.getUserId());
    String username = oldUser.getUsername();

    // 判断旧密码是否正确
    boolean isMatched = passwordEncoder.matches(query.getOldPassword(), oldUser.getHashedPassword());
    if (!isMatched) throw new BadRequestException("用户（" + username + "）旧密码错误");

    // 更新数据
    User toUpdate = new User();
    toUpdate.setUserId(oldUser.getUserId());
    toUpdate.setHashedPassword(passwordEncoder.encode(query.getNewPassword()));
    userMapper.update(toUpdate);

    return new HashMap<>() {{
      put("msg", "用户（" + username + "）密码修改成功");
    }};
  }

  /**
   * 删除用户。
   *
   * @param userId 需要删除的用户 id
   * @return 删除成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> deleteUser(int userId) {
    // 判断用户是否存在
    User toDel = getUserFromDbMustBeExists(userId);
    String username = toDel.getUsername();

    // 删除数据
    userMapper.deleteByUserId(userId);

    return new HashMap<>() {{
      put("msg", "用户（" + username + "）删除成功");
    }};
  }

  private User getUserFromDbMustBeExists(int userId) {
    return Optional.ofNullable(userMapper.findByUserId(userId))
      .orElseThrow(() -> new NotFoundException("未找到 id 为 " + userId + " 的用户"));
  }

  private Optional<User> getUserToUpdate(User oldUser, UpdateUserQuery query) {
    boolean needsUpdate = false;
    User toUpdate = new User();
    toUpdate.setUserId(oldUser.getUserId());

    YesOrNo newEnabled = YesOrNo.resolve(query.getEnabled()).orElse(null);
    if (newEnabled != null && newEnabled != oldUser.getEnabled()) {
      needsUpdate = true;
      toUpdate.setEnabled(newEnabled);
    }

    String newMenus = query.getMenus();
    if (newMenus != null && !StrUtils.equalsIgnoreBlank(newMenus, oldUser.getMenus())) {
      needsUpdate = true;
      toUpdate.setMenus(newMenus);
    }

    if (!needsUpdate) return Optional.empty();

    return Optional.of(toUpdate);
  }
}
