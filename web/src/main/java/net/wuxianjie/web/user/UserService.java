package net.wuxianjie.web.user;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.DataConflictException;
import net.wuxianjie.core.exception.NotFoundException;
import net.wuxianjie.core.paging.PagingData;
import net.wuxianjie.core.paging.PagingQuery;
import net.wuxianjie.core.shared.Written2Db;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.web.handler.EnumUtils;
import net.wuxianjie.web.operationlog.OperationLogService;
import net.wuxianjie.web.shared.YesOrNo;
import org.springframework.beans.factory.annotation.Autowired;
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

    public ManagementOfUser getUser(String username) {
        User user = userMapper.findUserByUsername(username);
        return new ManagementOfUser(user);
    }

    public PagingData<List<ManagementOfUser>> getUsers(PagingQuery paging, String fuzzyUsername) {
        List<User> users = userMapper.findByUsernameLimitModifyTimeDesc(paging, fuzzyUsername);

        int total = userMapper.countByUsername(fuzzyUsername);

        List<ManagementOfUser> userList = users.stream()
                .map(ManagementOfUser::new)
                .collect(Collectors.toList());

        return new PagingData<>(total, paging.getPageNo(), paging.getPageSize(), userList);
    }

    @Transactional(rollbackFor = Exception.class)
    public Written2Db addNewUser(ManagementOfUser userToAdd) {
        validateUsernameUniqueness(userToAdd.getUsername());

        String hashedPassword = passwordEncoder.encode(userToAdd.getPassword());

        userToAdd.setPassword(hashedPassword);

        User userToAddDb = createUserToAddDb(userToAdd);

        int addedNum = userMapper.add(userToAddDb);

        String logMessage = String.format("新增用户数据【ID：%s, 用户名：%s】",
                userToAddDb.getUserId(),
                userToAddDb.getUsername()
        );

        logService.addNewOperationLog(LocalDateTime.now(), logMessage);

        return new Written2Db(addedNum, "新增用户成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public Written2Db updateUser(ManagementOfUser userToUpdate) {
        User userToUpdateDb = getUserFromDbMustBeExists(userToUpdate.getUserId());

        List<String> logs = new ArrayList<>();
        boolean needsUpdate = needsUpdateLogsSetNull2Fields(userToUpdateDb, userToUpdate, logs);

        if (needsUpdate) {
            int updatedNum = userMapper.update(userToUpdateDb);

            String logMessage = String.format("修改用户数据【%s】", String.join("；", logs));

            logService.addNewOperationLog(LocalDateTime.now(), logMessage);

            return new Written2Db(updatedNum, "更新用户成功");
        }

        return new Written2Db(0, "无需更新用户");
    }

    @Transactional(rollbackFor = Exception.class)
    public Written2Db updateUserPassword(ManagementOfUser passwordToUpdate) {
        Integer userId = passwordToUpdate.getUserId();
        User userToUpdateDb = getUserFromDbMustBeExists(userId);

        checkOldPassword(passwordToUpdate.getOldPassword(), userToUpdateDb.getHashedPassword());

        String newPassword = passwordToUpdate.getNewPassword();
        String newHashedPassword = passwordEncoder.encode(newPassword);

        int updatedNum = userMapper.updatePasswordById(userId, newHashedPassword);

        String logMessage = String.format("修改用户【%s】密码", userToUpdateDb.getUsername());

        logService.addNewOperationLog(LocalDateTime.now(), logMessage);

        return new Written2Db(updatedNum, "修改密码成功");
    }

    @Transactional(rollbackFor = Exception.class)
    public Written2Db removeUser(int userId) {
        User userToUpdateDb = getUserFromDbMustBeExists(userId);

        int deletedNum = userMapper.deleteById(userId);

        String logMessage = String.format("删除用户数据【%s】", userToUpdateDb.getUsername());

        logService.addNewOperationLog(LocalDateTime.now(), logMessage);

        return new Written2Db(deletedNum, "删除用户成功");
    }

    private void validateUsernameUniqueness(String username) {
        boolean existsUsername = userMapper.existsUsername(username);

        if (existsUsername) {
            throw new DataConflictException("已存在相同用户名");
        }
    }

    private User createUserToAddDb(ManagementOfUser user) {
        User userToAdd = new User();

        BeanUtil.copyProperties(user, userToAdd, "enabled");

        userToAdd.setEnabled(EnumUtils.resolve(YesOrNo.class, user.getEnabled()));

        return userToAdd;
    }

    private User getUserFromDbMustBeExists(Integer userId) {
        User user = userMapper.findById(userId);

        if (user == null) {
            throw new NotFoundException(String.format("用户 ID【%s】不存在", userId));
        }

        return user;
    }

    private boolean needsUpdateLogsSetNull2Fields(
            User userToUpdateDb,
            ManagementOfUser userToUpdate,
            List<String> logs
    ) {
        boolean isPasswordChanged = isPasswordChangedLogSetNull(userToUpdateDb, userToUpdate, logs);

        boolean isRolesChanged = isRolesChangedLogSetNull(userToUpdateDb, userToUpdate, logs);

        return isPasswordChanged || isRolesChanged;
    }

    private void checkOldPassword(String rawPassword, String hashedPassword) {
        boolean isPasswordCorrect = passwordEncoder.matches(rawPassword, hashedPassword);

        if (!isPasswordCorrect) {
            throw new BadRequestException("旧密码错误");
        }
    }

    private boolean isPasswordChangedLogSetNull(
            User userToUpdateDb,
            ManagementOfUser userToUpdate,
            List<String> logs
    ) {
        boolean isChanged = false;
        String rawPassword = userToUpdate.getPassword();

        boolean isSamePassword = rawPassword != null &&
                passwordEncoder.matches(rawPassword, userToUpdateDb.getHashedPassword());

        if (rawPassword != null && !isSamePassword) {
            isChanged = true;

            String hashedPassword = passwordEncoder.encode(rawPassword);

            userToUpdateDb.setHashedPassword(hashedPassword);

            logs.add(String.format("重置用户【%s】密码", userToUpdateDb.getUsername()));
        } else if (rawPassword != null) {
            userToUpdateDb.setHashedPassword(null);
        }

        return isChanged;
    }

    private boolean isRolesChangedLogSetNull(
            User userToUpdateDb,
            ManagementOfUser userToUpdate,
            List<String> logs
    ) {
        boolean isChanged = false;
        String roles = userToUpdate.getRoles();

        boolean isSameRoles = StringUtils.isEqualsIgnoreNull(
                roles,
                userToUpdateDb.getRoles()
        );

        if (roles != null && !isSameRoles) {
            isChanged = true;

            userToUpdateDb.setRoles(roles);

            logs.add(String.format("将角色【%s】修改为【%s】", userToUpdateDb.getRoles(), roles));
        } else if (roles != null) {
            userToUpdateDb.setRoles(null);
        }

        return isChanged;
    }
}
