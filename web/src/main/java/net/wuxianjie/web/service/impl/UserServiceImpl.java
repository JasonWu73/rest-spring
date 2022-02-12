package net.wuxianjie.web.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.DataConflictException;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.web.controller.UserController;
import net.wuxianjie.web.mapper.UserMapper;
import net.wuxianjie.web.model.Account;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.model.Wrote2Database;
import net.wuxianjie.web.service.OperationLogService;
import net.wuxianjie.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现系统可登录用户的业务操作
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

  private final OperationLogService logService;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public PaginationData<List<User>> loadUsers(@NonNull final Integer pageNo, @NonNull final Integer pageSize, final String fuzzyUsername) {
    final List<User> users = userMapper.findUsersPaginationByUsername(pageNo * pageSize, pageSize, fuzzyUsername);
    final int total = userMapper.findUserCountByUsername(fuzzyUsername);
    return new PaginationData<>(total, pageNo, pageSize, users);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database saveUser(@NonNull final UserController.UserToAdd userToAdd) {
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

    // 记录日志
    final String logMessage = String.format("新增用户名为%s的用户", userToAdd.getUsername());
    logService.saveOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Database(addedNum, "新增用户成功");
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database updateUser(@NonNull final UserController.UserToUpdate userToUpdate) {
    // 若库中不存在该用户ID的数据，则直接退出方法
    final Account account = userMapper.findAccountByUserId(userToUpdate.getUserId());

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 判断数据是否需要更新
    final boolean shouldUpdate = shouldUpdateUserInfo(account, userToUpdate);
    // 将不需要更新的数据设置为null并返回详细日志
    final String updatedLog = makeNull4NotNeedUpdatedFiledAndReturnLog(account, userToUpdate);

    if (shouldUpdate) {
      // 入库
      final int updatedNum = userMapper.updateUser(userToUpdate);

      // 记录日志
      final String logMessage = String.format("更新用户信息：%s", updatedLog);
      logService.saveOperationLog(LocalDateTime.now(), logMessage);

      return new Wrote2Database(updatedNum, "更新用户成功");
    }

    return new Wrote2Database(0, "无需更新用户");
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database updatePassword(@NonNull final UserController.PasswordToUpdate passwordToUpdate) {
    // 若库中不存在该用户ID的数据，则直接退出方法
    final Account account = userMapper.findAccountByUserId(passwordToUpdate.getUserId());

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 若旧密码与库中的密码不匹配，则直接退出方法
    final boolean isRightOldPassword = passwordEncoder.matches(passwordToUpdate.getOldPassword(), account.getPassword());

    if (!isRightOldPassword) {
      throw new BadRequestException("旧密码错误");
    }

    // 编码明文新密码
    final String encodedNewPassword = passwordEncoder.encode(passwordToUpdate.getNewPassword());

    // 入库
    final int updatedNum = userMapper.updatePassword(passwordToUpdate.getUserId(), encodedNewPassword);

    // 记录日志
    final String logMessage = String.format("修改用户名为%s的密码", account.getName());
    logService.saveOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Database(updatedNum, "修改密码成功");
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database removeUser(final int userId) {
    // 若用户ID不存在，则直接返回
    final Account account = userMapper.findAccountByUserId(userId);

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 入库
    final int deletedNum = userMapper.deleteUserByUserId(userId);

    // 记录日志
    final String logMessage = String.format("删除用户名为%s的用户", account.getName());
    logService.saveOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Database(deletedNum, "删除用户成功");
  }

  private boolean shouldUpdateUserInfo(final Account account, final UserController.UserToUpdate userToUpdate) {
    final boolean isSamePassword = userToUpdate.getPassword() != null
        && passwordEncoder.matches(userToUpdate.getPassword(), account.getPassword());

    if (userToUpdate.getPassword() != null && !isSamePassword) {
      return true;
    }

    final boolean isSameRoles = StringUtils.isNullEquals(account.getRoles(), userToUpdate.getRoles());

    return userToUpdate.getRoles() != null && !isSameRoles;
  }

  private String makeNull4NotNeedUpdatedFiledAndReturnLog(final Account account, final UserController.UserToUpdate userToUpdate) {
    final List<String> logBuilder = new ArrayList<>();

    final boolean isSamePassword = userToUpdate.getPassword() != null
        && passwordEncoder.matches(userToUpdate.getPassword(), account.getPassword());

    if (userToUpdate.getPassword() != null && !isSamePassword) {
      // 编码明文密码
      final String encodedPassword = passwordEncoder.encode(userToUpdate.getPassword());
      userToUpdate.setPassword(encodedPassword);

      logBuilder.add(String.format("重置了%s的密码", account.getName()));
    } else if (userToUpdate.getPassword() != null){
      userToUpdate.setPassword(null);
    }

    final boolean isSameRoles = StringUtils.isNullEquals(account.getRoles(), userToUpdate.getRoles());

    if (userToUpdate.getRoles() != null && !isSameRoles) {
      logBuilder.add(String.format("将角色从%s修改为%s",
          account.getRoles(), userToUpdate.getRoles().isEmpty() ? "空" : userToUpdate.getRoles()));
    } else if (userToUpdate.getRoles() != null) {
      userToUpdate.setRoles(null);
    }

    return String.join("；", logBuilder);
  }
}
