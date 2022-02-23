package net.wuxianjie.web.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.DataConflictException;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.model.PaginationQuery;
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
 * 实现用户管理业务逻辑
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
  public PaginationData<List<User>> getUsers(@NonNull final PaginationQuery pagination,
                                             final String fuzzyUsername) {
    // 根据分页条件及用户名从数据库中获取用户列表数据
    final List<User> users = userMapper.findByPagination(pagination, fuzzyUsername);

    // 根据用户名从数据库中统计用户总数
    final int total = userMapper.countByUsername(fuzzyUsername);
    return new PaginationData<>(total, pagination.getPageNo(), pagination.getPageSize(), users);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database saveUser(@NonNull final UserController.UserToAdd userToAdd) {
    // 若已存在同名用户，则直接退出
    final int count = userMapper.countByUsername(userToAdd.getUsername());

    if (count > 0) {
      throw new DataConflictException("已存在相同用户名");
    }

    // 将明文密码编码为哈希值
    final String encodedPassword = passwordEncoder.encode(userToAdd.getPassword());
    userToAdd.setPassword(encodedPassword);

    // 将用户数据插入数据库中
    final int addedNum = userMapper.save(userToAdd);

    // 记录操作日志
    final String logMessage = String.format("新增用户【%s】", userToAdd.getUsername());
    logService.saveOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Database(addedNum, "新增用户成功");
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database updateUser(@NonNull final UserController.UserToUpdate userToUpdate) {
    // 若库中不存在该用户ID的数据，则直接退出
    final Account account = userMapper.findById(userToUpdate.getUserId());

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 判断数据是否需要更新
    final boolean needsUpdate = needsUpdateUserInfo(account, userToUpdate);

    // 将不需要更新的数据设置为null并返回详细操作日志
    final String updatedLog = makeNull4NotNeedUpdatedFiledAndReturnLog(account, userToUpdate);

    if (needsUpdate) {
      // 更新数据库中的用户数据
      final int updatedNum = userMapper.update(userToUpdate);

      // 记录操作日志
      final String logMessage = String.format("更新用户信息【%s】", updatedLog);
      logService.saveOperationLog(LocalDateTime.now(), logMessage);

      return new Wrote2Database(updatedNum, "更新用户成功");
    }

    return new Wrote2Database(0, "无需更新用户");
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database updatePassword(@NonNull final UserController.PasswordToUpdate passwordToUpdate) {
    // 若库中不存在该用户ID的数据，则直接退出
    final Account account = userMapper.findById(passwordToUpdate.getUserId());

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 若旧密码与库中的密码不匹配，则直接退出
    final boolean isRightOldPassword = passwordEncoder
        .matches(passwordToUpdate.getOldPassword(), account.getHashedPassword());

    if (!isRightOldPassword) {
      throw new BadRequestException("旧密码错误");
    }

    // 将明文新密码编码为哈希值
    final String encodedNewPassword = passwordEncoder.encode(passwordToUpdate.getNewPassword());

    // 更新数据库中的密码
    final int updatedNum = userMapper.updatePasswordById(passwordToUpdate.getUserId(), encodedNewPassword);

    // 记录操作日志
    final String logMessage = String.format("修改用户【%s】的密码", account.getAccountName());
    logService.saveOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Database(updatedNum, "修改密码成功");
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database removeUser(final int userId) {
    // 若用户ID不存在，则直接返回
    final Account account = userMapper.findById(userId);

    if (account == null) {
      throw new BadRequestException("用户ID不存在");
    }

    // 从数据库中删除指定用户
    final int deletedNum = userMapper.deleteById(userId);

    // 记录操作日志
    final String logMessage = String.format("删除用户【%s】", account.getAccountName());
    logService.saveOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Database(deletedNum, "删除用户成功");
  }

  private boolean needsUpdateUserInfo(final Account account, final UserController.UserToUpdate userToUpdate) {
    // 判断密码是否需要更改
    final boolean isSamePassword = userToUpdate.getPassword() != null
        && passwordEncoder.matches(userToUpdate.getPassword(), account.getHashedPassword());

    if (userToUpdate.getPassword() != null && !isSamePassword) {
      return true;
    }

    // 判断角色是否需要更改
    final boolean isSameRoles = StringUtils.isNullEquals(account.getRoles(), userToUpdate.getRoles());

    return userToUpdate.getRoles() != null && !isSameRoles;
  }

  private String makeNull4NotNeedUpdatedFiledAndReturnLog(final Account account, final UserController.UserToUpdate userToUpdate) {
    final List<String> logBuilder = new ArrayList<>();

    // 判断密码是否需要更改
    final boolean isSamePassword = userToUpdate.getPassword() != null
        && passwordEncoder.matches(userToUpdate.getPassword(), account.getHashedPassword());

    if (userToUpdate.getPassword() != null && !isSamePassword) {
      // 将明文密码编码为哈希值
      final String encodedPassword = passwordEncoder.encode(userToUpdate.getPassword());
      userToUpdate.setPassword(encodedPassword);

      logBuilder.add(String.format("重置用户【%s】的密码", account.getAccountName()));
    } else if (userToUpdate.getPassword() != null){
      userToUpdate.setPassword(null);
    }

    // 判断角色是否需要更改
    final boolean isSameRoles = StringUtils.isNullEquals(account.getRoles(), userToUpdate.getRoles());

    if (userToUpdate.getRoles() != null && !isSameRoles) {
      logBuilder.add(String.format("将角色从【%s】修改为【%s】", account.getRoles(), userToUpdate.getRoles()));
    } else if (userToUpdate.getRoles() != null) {
      userToUpdate.setRoles(null);
    }

    // 返回详细操作日志
    return String.join("；", logBuilder);
  }
}
