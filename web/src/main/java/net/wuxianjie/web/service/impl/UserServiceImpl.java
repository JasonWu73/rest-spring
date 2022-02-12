package net.wuxianjie.web.service.impl;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.DataConflictException;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.web.controller.UserController;
import net.wuxianjie.web.mapper.UserMapper;
import net.wuxianjie.web.model.Account;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.model.WroteDb;
import net.wuxianjie.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 实现系统可登录用户的业务操作
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public PaginationData<List<User>> loadUsers(@NotNull final Integer pageNo, @NotNull final Integer pageSize, final String fuzzyUsername) {
    final List<User> users = userMapper.findUsersPaginationByUsername(pageNo * pageSize, pageSize, fuzzyUsername);
    final int total = userMapper.findUserCountByUsername(fuzzyUsername);
    return new PaginationData<>(total, pageNo, pageSize, users);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public WroteDb saveUser(@NotNull final UserController.UserToAdd userToAdd) {
    // 若已存在同名用户，则拒绝添加
    final int count = userMapper.findUserCountByUsername(userToAdd.getUsername());

    if (count > 0) {
      throw new DataConflictException("已存在相同用户名");
    }

    // 编码明文密码
    final String encodedPassword = passwordEncoder.encode(userToAdd.getPassword());
    userToAdd.setPassword(encodedPassword);

    // 入库
    final int addedNum = userMapper.addUser(userToAdd);

    return new WroteDb(addedNum, "新增用户成功");
  }

  @Override
  @Transactional
  public WroteDb updateUser(@NotNull final UserController.UserToUpdate userToUpdate) {
    // 若库中不存在该用户ID的数据，则直接退出方法
    final Account account = userMapper.findAccountByUserId(userToUpdate.getUserId());

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 判断数据是否需要更新，并将不需要更新的数据设置为null
    final boolean shouldUpdate = shouldUpdateAndSetNull4NotNeeded(account, userToUpdate);

    // 入库
    if (shouldUpdate) {
      final int updatedNum = userMapper.updateUser(userToUpdate);
      return new WroteDb(updatedNum, "更新用户成功");
    }

    return new WroteDb(0, "无需更新用户");
  }

  @Override
  public WroteDb updatePassword(@NotNull final UserController.PasswordToUpdate passwordToUpdate) {
    // 若库中不存在该用户ID的数据，则直接退出方法
    final Account account = userMapper.findAccountByUserId(passwordToUpdate.getUserId());

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 若旧密码与库中的密码不匹配，则直接退出方法
    final boolean isRightOldPassword = passwordEncoder.matches(passwordToUpdate.getOldPassword(), account.getAccountPassword());

    if (!isRightOldPassword) {
      throw new BadRequestException("旧密码错误");
    }

    // 编码明文新密码
    final String encodedNewPassword = passwordEncoder.encode(passwordToUpdate.getNewPassword());

    // 入库
    final int updatedNum = userMapper.updatePassword(passwordToUpdate.getUserId(), encodedNewPassword);

    return new WroteDb(updatedNum, "修改密码成功");
  }

  @Override
  @Transactional
  public WroteDb removeUser(final int userId) {
    final int deletedNum = userMapper.deleteUserByUserId(userId);
    return new WroteDb(deletedNum, deletedNum == 0 ? "无需删除用户" : "删除用户成功");
  }

  /**
   * 判断是否需要更新数据库中数据，并将不需要更新的字段设置为null
   */
  private boolean shouldUpdateAndSetNull4NotNeeded(final Account account, final UserController.UserToUpdate userToUpdate) {
    boolean shouldUpdate = false;

    final boolean isSamePassword = userToUpdate.getPassword() != null && passwordEncoder.matches(userToUpdate.getPassword(), account.getAccountPassword());
    if (userToUpdate.getPassword() != null && !isSamePassword) {
      shouldUpdate = true;

      // 编码明文密码
      final String encodedPassword = passwordEncoder.encode(userToUpdate.getPassword());
      userToUpdate.setPassword(encodedPassword);
    } else {
      userToUpdate.setPassword(null);
    }

    final boolean isSameRoles = StringUtils.isNullEquals(account.getAccountRoles(), userToUpdate.getRoles());
    if (userToUpdate.getRoles() != null && !isSameRoles) {
      shouldUpdate = true;
    } else {
      userToUpdate.setRoles(null);
    }

    return shouldUpdate;
  }
}
