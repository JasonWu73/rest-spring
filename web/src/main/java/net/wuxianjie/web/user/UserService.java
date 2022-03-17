package net.wuxianjie.web.user;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.handler.YesOrNo;
import net.wuxianjie.springbootcore.paging.PagingData;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.shared.*;
import net.wuxianjie.web.operationlog.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

  private final UserMapper userMapper;
  private final OperationLogService logService;
  private final PasswordEncoder passwordEncoder;

  public Optional<User> getUser(String username) {
    final User user = userMapper.findUserByUsername(username);

    return Optional.ofNullable(user);
  }

  public PagingData<List<ListOfUserItem>> getUsers(PagingQuery paging,
                                                   GetUserQuery query) {
    final List<ListOfUserItem> users =
        userMapper.findByQueryPagingModifyTimeDesc(paging, query);

    final int total = userMapper.countByQuery(query);

    return new PagingData<>(paging, total, users);
  }

  @Transactional(rollbackFor = Exception.class)
  public Wrote2Db addNewUser(AddOrUpdateUserQuery query) {
    validateUsernameUniqueness(query.getUsername());

    final User userToAdd = createUserToAdd(query);
    final int addedNum = userMapper.add(userToAdd);

    final String logMessage = String.format("新增用户数据【ID：%s, 用户名：%s】",
        userToAdd.getUserId(), userToAdd.getUsername());

    logService.addNewOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Db(addedNum, "新增用户成功");
  }

  @Transactional(rollbackFor = Exception.class)
  public Wrote2Db updateUser(AddOrUpdateUserQuery query) {
    final Integer userId = query.getUserId();
    final User userToUpdate = getUserFromDbMustBeExists(userId);
    final String username = userToUpdate.getUsername();

    final List<String> logs = new ArrayList<>();
    final boolean needsUpdate = needsUpdateUser(userToUpdate, query, logs);

    if (needsUpdate) {
      final int updatedNum = userMapper.update(userToUpdate);

      final String logMessage = String.format(
          "修改用户数据【ID：%s，用户名：%s】：%s",
          userId, username, String.join("；", logs));

      logService.addNewOperationLog(LocalDateTime.now(), logMessage);

      return new Wrote2Db(updatedNum, "修改用户成功");
    }

    return new Wrote2Db(0, "无需修改用户");
  }

  @Transactional(rollbackFor = Exception.class)
  public Wrote2Db updateUserPassword(UpdatePasswordQuery query) {
    final Integer userId = query.getUserId();
    final User passwordToUpdate = getUserFromDbMustBeExists(userId);
    final String username = passwordToUpdate.getUsername();

    validatePassword(query.getOldPassword(),
        passwordToUpdate.getHashedPassword());

    final int updatedNum = updateUserPasswordInDatabase(query);

    final String logMessage = String.format("修改用户密码【ID：%s，用户名：%s】",
        userId, username);

    logService.addNewOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Db(updatedNum, "修改密码成功");
  }

  @Transactional(rollbackFor = Exception.class)
  public Wrote2Db deleteUser(int userId) {
    final User userToDelete = getUserFromDbMustBeExists(userId);

    final int deletedNum = userMapper.deleteById(userId);

    final String logMessage = String.format("删除用户数据【ID：%s，用户名：%s】",
        userToDelete.getUserId(), userToDelete.getUsername());

    logService.addNewOperationLog(LocalDateTime.now(), logMessage);

    return new Wrote2Db(deletedNum, "删除用户成功");
  }

  private void validateUsernameUniqueness(String username) {
    final boolean existsUsername = userMapper.existsUsername(username);

    if (existsUsername) {
      throw new DataConflictException(
          String.format("用户名【%s】已存在", username));
    }
  }

  private User createUserToAdd(AddOrUpdateUserQuery query) {
    final User userToAdd = new User();

    BeanUtil.copyProperties(query, userToAdd, "enabled");

    userToAdd.setHashedPassword(passwordEncoder.encode(query.getPassword()));
    userToAdd.setEnabled(EnumUtils.resolve(YesOrNo.class, query.getEnabled())
        .orElseThrow());

    return userToAdd;
  }

  private User getUserFromDbMustBeExists(Integer userId) {
    final User user = userMapper.findById(userId);

    if (user == null) {
      throw new NotFoundException(String.format("用户 ID【%s】不存在", userId));
    }

    return user;
  }

  private boolean needsUpdateUser(User userToUpdate,
                                  AddOrUpdateUserQuery query,
                                  List<String> logs) {
    final boolean needsUpdatePassword =
        needsUpdatePassword(userToUpdate, query, logs);

    final boolean needsUpdateRoles = needsUpdateRoles(userToUpdate, query, logs);

    final boolean needsUpdateEnabled = needsUpdateEnabled(userToUpdate, query, logs);

    return needsUpdatePassword || needsUpdateRoles || needsUpdateEnabled;
  }

  private void validatePassword(String rawPassword, String hashedPassword) {
    final boolean isPasswordCorrect =
        passwordEncoder.matches(rawPassword, hashedPassword);

    if (!isPasswordCorrect) {
      throw new BadRequestException("旧密码错误");
    }
  }

  private int updateUserPasswordInDatabase(UpdatePasswordQuery query) {
    final String rawPassword = query.getNewPassword();
    final String hashedPassword = passwordEncoder.encode(rawPassword);

    final User user = new User();

    user.setUserId(query.getUserId());
    user.setHashedPassword(hashedPassword);

    return userMapper.update(user);
  }

  private boolean needsUpdatePassword(User userToUpdate,
                                      AddOrUpdateUserQuery query,
                                      List<String> logs) {
    boolean isChanged = false;

    final String rawPassword = query.getPassword();
    final String oldHashedPassword1 = userToUpdate.getHashedPassword();

    final boolean isSamePassword = rawPassword != null
        && passwordEncoder.matches(rawPassword, oldHashedPassword1);

    if (rawPassword != null && !isSamePassword) {
      isChanged = true;

      logs.add("重置密码");

      final String hashedPassword = passwordEncoder.encode(rawPassword);

      userToUpdate.setHashedPassword(hashedPassword);
    } else if (rawPassword != null) {
      userToUpdate.setHashedPassword(null);
    }

    return isChanged;
  }

  private boolean needsUpdateRoles(User userToUpdate,
                                   AddOrUpdateUserQuery query,
                                   List<String> logs) {
    boolean isChanged = false;

    final String roles = query.getRoles();
    final String oldRoles = userToUpdate.getRoles();

    final boolean isSameRoles = StrUtils.isEqualsIgnoreNull(roles, oldRoles);

    if (roles != null && !isSameRoles) {
      isChanged = true;

      logs.add(String.format("将角色【%s】修改为【%s】", oldRoles, roles));

      userToUpdate.setRoles(roles);
    } else if (roles != null) {
      userToUpdate.setRoles(null);
    }

    return isChanged;
  }

  private boolean needsUpdateEnabled(User userToUpdate,
                                     AddOrUpdateUserQuery query,
                                     List<String> logs) {
    boolean isChanged = false;

    final YesOrNo enabled = EnumUtils.resolve(YesOrNo.class, query.getEnabled())
        .orElse(null);
    final YesOrNo oldEnabled = userToUpdate.getEnabled();

    final boolean isSameEnabled = enabled != null && enabled == oldEnabled;

    if (enabled != null && !isSameEnabled) {
      isChanged = true;

      logs.add(String.format("将启用状态【%s】修改为【%s】",
          oldEnabled.name(), enabled.name()));

      userToUpdate.setEnabled(enabled);
    } else if (enabled != null) {
      userToUpdate.setEnabled(null);
    }

    return isChanged;
  }
}
