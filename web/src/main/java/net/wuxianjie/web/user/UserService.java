package net.wuxianjie.web.user;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.paging.PagingData;
import net.wuxianjie.core.paging.PagingQuery;
import net.wuxianjie.core.shared.BadRequestException;
import net.wuxianjie.core.shared.DataConflictException;
import net.wuxianjie.core.shared.NotFoundException;
import net.wuxianjie.core.shared.Wrote2Db;
import net.wuxianjie.core.util.StrUtils;
import net.wuxianjie.web.handler.EnumUtils;
import net.wuxianjie.web.operationlog.OperationLogService;
import net.wuxianjie.web.shared.YesOrNo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserMapper userMapper;
    private final OperationLogService logService;
    private final PasswordEncoder passwordEncoder;

    @Nullable
    public ManagementOfUser getUser(String username) {
        final User user = userMapper.findUserByUsername(username);

        return user == null ? null : new ManagementOfUser(user);
    }

    @NonNull
    public PagingData<List<ManagementOfUser>> getUsers(PagingQuery paging, ManagementOfUser query) {
        final List<User> users = userMapper.findByUsernameEnabledLimitModifyTimeDesc(paging,
                query.getUsername(), query.getEnabled());

        final int total = userMapper.countByUsernameEnabled(query.getUsername(), query.getEnabled());

        final List<ManagementOfUser> userList = users.stream()
                .map(ManagementOfUser::new)
                .collect(Collectors.toList());

        return new PagingData<>(total, paging.getPageNo(), paging.getPageSize(), userList);
    }

    @NonNull
    @Transactional(rollbackFor = Exception.class)
    public Wrote2Db addNewUser(ManagementOfUser query) {
        checkUsernameExists(query.getUsername());

        final String hashedPassword = passwordEncoder.encode(query.getPassword());

        query.setPassword(hashedPassword);

        final User userToAdd = createUserToAdd(query);

        final int addedNum = userMapper.add(userToAdd);

        final String logMessage = String.format("新增用户数据【ID：%s, 用户名：%s】",
                userToAdd.getUserId(), userToAdd.getUsername());

        logService.addNewOperationLog(LocalDateTime.now(), logMessage);

        return new Wrote2Db(addedNum, "新增用户成功");
    }

    @NonNull
    @Transactional(rollbackFor = Exception.class)
    public Wrote2Db updateUser(ManagementOfUser query) {
        final User userToUpdate = getUserFromDbMustBeExists(query.getUserId());

        final List<String> logs = new ArrayList<>();
        final boolean needsUpdate = needsUpdateLogsSetNull2Fields(userToUpdate, query, logs);

        if (needsUpdate) {
            final int updatedNum = userMapper.update(userToUpdate);

            final String logMessage = String.format("修改用户【ID：%s，用户名：%s】数据：%s",
                    userToUpdate.getUserId(), userToUpdate.getUsername(), String.join("；", logs));

            logService.addNewOperationLog(LocalDateTime.now(), logMessage);

            return new Wrote2Db(updatedNum, "更新用户成功");
        }

        return new Wrote2Db(0, "无需更新用户");
    }

    @NonNull
    @Transactional(rollbackFor = Exception.class)
    public Wrote2Db updateUserPassword(ManagementOfUser query) {
        final Integer userId = query.getUserId();
        final User passwordToUpdate = getUserFromDbMustBeExists(userId);

        checkOldPassword(query.getOldPassword(), passwordToUpdate.getHashedPassword());

        final String newPassword = query.getNewPassword();
        final String newHashedPassword = passwordEncoder.encode(newPassword);

        final int updatedNum = userMapper.updatePasswordById(userId, newHashedPassword);

        final String logMessage = String.format("修改用户【ID：%s，用户名：%s】密码",
                passwordToUpdate.getUserId(), passwordToUpdate.getUsername());

        logService.addNewOperationLog(LocalDateTime.now(), logMessage);

        return new Wrote2Db(updatedNum, "修改密码成功");
    }

    @NonNull
    @Transactional(rollbackFor = Exception.class)
    public Wrote2Db deleteUser(int userId) {
        final User userToDelete = getUserFromDbMustBeExists(userId);

        final int deletedNum = userMapper.deleteById(userId);

        final String logMessage = String.format("删除用户数据【ID：%s，用户名：%s】",
                userToDelete.getUsername(), userToDelete.getUsername());

        logService.addNewOperationLog(LocalDateTime.now(), logMessage);

        return new Wrote2Db(deletedNum, "删除用户成功");
    }

    private void checkUsernameExists(String username) {
        final boolean existsUsername = userMapper.existsUsername(username);

        if (existsUsername) {
            throw new DataConflictException(String.format("用户名【%s】已存在", username));
        }
    }

    @NonNull
    private User createUserToAdd(ManagementOfUser query) {
        final User userToAdd = new User();

        BeanUtil.copyProperties(query, userToAdd, "enabled");

        userToAdd.setEnabled(EnumUtils.resolve(YesOrNo.class, query.getEnabled()));

        return userToAdd;
    }

    @NonNull
    private User getUserFromDbMustBeExists(Integer userId) {
        final User user = userMapper.findById(userId);

        if (user == null) {
            throw new NotFoundException(String.format("用户 ID【%s】不存在", userId));
        }

        return user;
    }

    private boolean needsUpdateLogsSetNull2Fields(User userToUpdate,
                                                  ManagementOfUser query,
                                                  List<String> logs) {
        boolean isPasswordChanged = isPasswordChangedLogSetNull(userToUpdate, query, logs);

        boolean isRolesChanged = isRolesChangedLogSetNull(userToUpdate, query, logs);

        boolean isEnabledChanged = isEnabledChangedLogSetNull(userToUpdate, query, logs);

        return isPasswordChanged || isRolesChanged || isEnabledChanged;
    }

    private void checkOldPassword(String rawPassword, String hashedPassword) {
        final boolean isPasswordCorrect = passwordEncoder.matches(rawPassword, hashedPassword);

        if (!isPasswordCorrect) {
            throw new BadRequestException("旧密码错误");
        }
    }

    private boolean isPasswordChangedLogSetNull(User userToUpdate,
                                                ManagementOfUser query,
                                                List<String> logs) {
        boolean isChanged = false;
        final String rawPassword = query.getPassword();

        boolean isSamePassword = rawPassword != null &&
                passwordEncoder.matches(rawPassword, userToUpdate.getHashedPassword());

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

    private boolean isRolesChangedLogSetNull(User userToUpdate,
                                             ManagementOfUser query,
                                             List<String> logs) {
        boolean isChanged = false;
        final String roles = query.getRoles();

        final boolean isSameRoles = StrUtils.isEqualsIgnoreNull(roles, userToUpdate.getRoles());

        if (roles != null && !isSameRoles) {
            isChanged = true;

            logs.add(String.format("将角色【%s】修改为【%s】", userToUpdate.getRoles(), roles));

            userToUpdate.setRoles(roles);
        } else if (roles != null) {
            userToUpdate.setRoles(null);
        }

        return isChanged;
    }

    private boolean isEnabledChangedLogSetNull(User userToUpdate,
                                               ManagementOfUser query,
                                               List<String> logs) {
        boolean isChanged = false;
        final YesOrNo enabled = EnumUtils.resolve(YesOrNo.class, query.getEnabled());

        final boolean isSameEnabled = enabled != null &&
                enabled == userToUpdate.getEnabled();

        if (enabled != null && !isSameEnabled) {
            isChanged = true;

            logs.add(String.format("将启用状态【%s】修改为【%s】", userToUpdate.getEnabled().name(), enabled.name()));

            userToUpdate.setEnabled(enabled);
        } else if (enabled != null) {
            userToUpdate.setEnabled(null);
        }

        return isChanged;
    }
}
