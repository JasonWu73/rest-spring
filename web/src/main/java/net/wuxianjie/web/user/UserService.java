package net.wuxianjie.web.user;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.exception.ConflictException;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.paging.RequestOfPaging;
import net.wuxianjie.springbootcore.paging.ResultOfPaging;
import net.wuxianjie.springbootcore.util.StringUtils;
import net.wuxianjie.web.security.SysMenu;
import net.wuxianjie.web.shared.SimpleResultOfWriteOperation;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  /**
   * 获取用户列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 用户列表
   */
  public ResultOfPaging<ListItemOfUser> getUsers(RequestOfPaging paging, RequestOfGetUser query) {
    List<ListItemOfUser> users = userMapper.findByUsernameLikeAndEnabledOrderByModifyTimeDesc(paging, query);
    int total = userMapper.countByUsernameLikeAndEnabled(query);
    return new ResultOfPaging<>(paging, total, users);
  }

  /**
   * 新增用户。
   *
   * @param query 需要保存的用户数据
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation saveUser(RequestOfSaveUser query) {
    // 校验并去重菜单编号字符串
    toDeduplicatedCommaSeparatedMenus(query.getMenus())
      .ifPresent(query::setMenus);

    // 用户名唯一性校验
    String username = query.getUsername();
    boolean isExisted = userMapper.existsByUsername(username);
    if (isExisted) throw new ConflictException(StrUtil.format("已存在相同用户名 [{}]", username));

    // 保存用户数据
    User userToSave = new User();
    userToSave.setEnabled(YesOrNo.resolve(query.getEnabled()).orElseThrow());
    userToSave.setUsername(query.getUsername());
    userToSave.setHashedPassword(passwordEncoder.encode(query.getPassword()));
    userToSave.setMenus(query.getMenus());
    userMapper.save(userToSave);

    return new SimpleResultOfWriteOperation(StrUtil.format("新增用户 [{}]", username));
  }

  /**
   * 修改用户。
   *
   * @param query 需要更新的用户数据
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation updateUser(RequestOfUpdateUser query) {
    // 校验并去重菜单编号字符串
    toDeduplicatedCommaSeparatedMenus(query.getMenus())
      .ifPresent(query::setMenus);

    // 检查用户是否存在
    User oldUser = getUserFromDatabaseMustBeExists(query.getUserId());
    String username = oldUser.getUsername();

    // 检查是否需要更新
    Optional<User> userToUpdate = getUserToUpdate(oldUser, query);
    if (userToUpdate.isEmpty()) return new SimpleResultOfWriteOperation("无需修改");

    // 更新用户数据
    userMapper.update(userToUpdate.get());

    return new SimpleResultOfWriteOperation(StrUtil.format("修改用户 [{}]", username));
  }

  /**
   * 重置用户密码。
   *
   * @param query 需要更新的用户数据
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation updateUserPassword(RequestOfResetUserPassword query) {
    // 检查用户是否存在
    User oldUser = getUserFromDatabaseMustBeExists(query.getUserId());
    String username = oldUser.getUsername();

    // 检查是否需要更新
    String newRawPassword = query.getPassword();
    boolean isMatched = passwordEncoder.matches(newRawPassword, oldUser.getHashedPassword());
    if (isMatched) return new SimpleResultOfWriteOperation("无需重置密码");

    // 更新角色数据
    User userToUpdate = new User();
    userToUpdate.setUserId(oldUser.getUserId());
    userToUpdate.setHashedPassword(passwordEncoder.encode(newRawPassword));
    userMapper.update(userToUpdate);

    return new SimpleResultOfWriteOperation(StrUtil.format("重置用户密码 [{}]", username));
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 新旧密码数据
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation updateCurrentUserPassword(RequestOfUpdateUserPwd query) {
    // 检查用户是否存在
    User oldUser = getUserFromDatabaseMustBeExists(query.getUserId());
    String username = oldUser.getUsername();

    // 检查旧密码是否正确
    boolean isMatched = passwordEncoder.matches(query.getOldPassword(), oldUser.getHashedPassword());
    if (!isMatched) throw new BadRequestException("旧密码错误");

    // 更新数据
    User userToUpdate = new User();
    userToUpdate.setUserId(oldUser.getUserId());
    userToUpdate.setHashedPassword(passwordEncoder.encode(query.getNewPassword()));
    userMapper.update(userToUpdate);

    return new SimpleResultOfWriteOperation(StrUtil.format("修改用户密码 [{}]", username));
  }

  /**
   * 删除用户。
   *
   * @param userId 需要删除的用户 id
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation deleteUser(int userId) {
    // 检查用户是否存在
    User toDel = getUserFromDatabaseMustBeExists(userId);
    String username = toDel.getUsername();

    // 删除数据
    userMapper.deleteByUserId(userId);

    return new SimpleResultOfWriteOperation(StrUtil.format("删除用户 [{}]", username));
  }

  /**
   * 获取用户数据，若用户不存在则抛出异常。
   *
   * @param username 用户名
   * @return 指定用户名的用户数据
   * @throws NotFoundException 当数据库中找不到指定用户名时抛出
   */
  public User getUserFromDatabaseMustBeExists(String username) throws NotFoundException {
    return Optional.ofNullable(userMapper.findByUsername(username))
      .orElseThrow(() -> new NotFoundException(StrUtil.format("未找到用户 [{}]", username)));
  }

  /**
   * 校验并去重菜单编号字符串。
   *
   * @param commaSeparatedMenus 以英文逗号分隔的菜单编号字符串
   * @return 以英文逗号分隔的菜单编号字符串的 {@link Optional} 包装对象
   */
  public Optional<String> toDeduplicatedCommaSeparatedMenus(String commaSeparatedMenus) {
    return Optional.ofNullable(StrUtil.trimToNull(commaSeparatedMenus))
      .flatMap(m -> {
        String[] menus = StrSplitter.splitToArray(m, ',', 0, true, true);

        if (menus.length == 0) return Optional.empty();

        boolean hasAnyInvalidMenu = Arrays.stream(menus)
          .anyMatch(menu -> SysMenu.resolve(menu).isEmpty());

        if (hasAnyInvalidMenu) throw new BadRequestException("包含非法菜单编号");

        return Optional.of(Arrays.stream(menus)
          .distinct()
          .collect(Collectors.joining(",")));
      });
  }

  private User getUserFromDatabaseMustBeExists(int userId) {
    return Optional.ofNullable(userMapper.findByUserId(userId))
      .orElseThrow(() -> new NotFoundException(StrUtil.format("未找到用户 [userId={}]", userId)));
  }

  private Optional<User> getUserToUpdate(User oldUser, RequestOfUpdateUser query) {
    boolean needsUpdate = false;

    User userToUpdate = new User();
    userToUpdate.setUserId(oldUser.getUserId());

    YesOrNo newEnabled = YesOrNo.resolve(query.getEnabled()).orElse(null);
    if (newEnabled != null && newEnabled != oldUser.getEnabled()) {
      needsUpdate = true;
      userToUpdate.setEnabled(newEnabled);
    }

    String newMenus = query.getMenus();
    if (newMenus != null && !StringUtils.equalsIgnoreBlank(newMenus, oldUser.getMenus())) {
      needsUpdate = true;
      userToUpdate.setMenus(newMenus);
    }

    if (!needsUpdate) return Optional.empty();

    return Optional.of(userToUpdate);
  }
}
