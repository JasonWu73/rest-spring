package net.wuxianjie.web.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.exception.ConflictException;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import net.wuxianjie.springbootcore.util.StrUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户管理。
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
   * @param query  查询参数
   * @return 用户列表
   */
  public PagingResult<UserDto> getUsers(PagingQuery paging,
                                        GetUserQuery query) {
    List<UserDto> users = userMapper.findByUsernameLikeAndEnabled(paging, query);

    int total = userMapper.countByUsernameLikeAndEnabled(query);

    return new PagingResult<>(paging, total, users);
  }

  /**
   * 新增用户。
   *
   * @param query 查询参数
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveUser(UserQuery query) {
    boolean isExists = userMapper.existsUserByName(query.getUsername());
    if (isExists) {
      throw new ConflictException("已存在相同用户名");
    }

    User user = new User();
    BeanUtil.copyProperties(query, user, "enabled");
    user.setHashedPassword(passwordEncoder.encode(query.getPassword()));
    user.setEnabled(YesOrNo.resolve(query.getEnabled()).orElseThrow());

    userMapper.insertUser(user);
  }

  /**
   * 修改用户。
   * <p>
   * 注意：此处为重置密码，即无需验证旧密码。
   * </p>
   *
   * @param query 查询参数
   */
  @Transactional(rollbackFor = Exception.class)
  public void updateUser(UserQuery query) {
    User user = getUserFromDbMustBeExists(query.getUserId());

    boolean needsUpdate = needsUpdateUser(user, query);
    if (!needsUpdate) return;

    userMapper.updateUser(user);
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 查询参数
   */
  @Transactional(rollbackFor = Exception.class)
  public void updatePassword(UserQuery query) {
    User user = getUserFromDbMustBeExists(query.getUserId());

    boolean isMatched = passwordEncoder.matches(query.getOldPassword(), user.getHashedPassword());
    if (!isMatched) {
      throw new BadRequestException("旧密码错误");
    }

    String rawPassword = query.getNewPassword();
    String hashedPassword = passwordEncoder.encode(rawPassword);
    user.setHashedPassword(hashedPassword);

    userMapper.updateUser(user);
  }

  /**
   * 删除用户。
   *
   * @param userId 用户 id
   */
  @Transactional(rollbackFor = Exception.class)
  public void removeUser(int userId) {
    userMapper.deleteUserById(userId);
  }

  private User getUserFromDbMustBeExists(int userId) {
    return Optional.ofNullable(userMapper.selectUserById(userId))
      .orElseThrow(() -> new NotFoundException(StrUtil.format("未找到 id 为 {} 的用户", userId)));
  }

  private boolean needsUpdateUser(User user, UserQuery query) {
    boolean needsUpdate = false;

    String newPassword = query.getPassword();
    if (newPassword != null && !passwordEncoder.matches(newPassword, user.getHashedPassword())) {
      needsUpdate = true;
      user.setHashedPassword(passwordEncoder.encode(newPassword));
    }

    String newRoles = query.getRoles();
    if (newRoles != null && !StrUtils.equalsIgnoreBlank(newRoles, user.getRoles())) {
      needsUpdate = true;
      user.setRoles(newRoles);
    }

    YesOrNo newEnabled = YesOrNo.resolve(query.getEnabled()).orElse(null);
    if (newEnabled != null && newEnabled != user.getEnabled()) {
      needsUpdate = true;
      user.setEnabled(newEnabled);
    }

    return needsUpdate;
  }
}
