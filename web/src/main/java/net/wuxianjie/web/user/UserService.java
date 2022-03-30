package net.wuxianjie.web.user;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.shared.*;
import net.wuxianjie.web.operationlog.OperationLogService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final OperationLogService logService;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getUser(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }

    public PagingResult<UserListItemDto> getUsers(PagingQuery paging, GetUserQuery query) {
        List<UserListItemDto> users = userMapper.findByQueryPagingOrderByModifyTimeDesc(paging, query);
        int total = userMapper.countByQuery(query);
        return new PagingResult<>(paging, total, users);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNewUser(AddOrUpdateUserQuery query) {
        verifyUsernameUniqueness(query.getUsername());

        User userToAdd = createUserToAdd(query);
        int addedNum = userMapper.add(userToAdd);
        if (addedNum <= 0) {
            throw new InternalException("用户新增失败");
        }

        Integer userId = userToAdd.getUserId();
        String username = userToAdd.getUsername();
        String message = String.format("新增用户数据【ID：%s，用户名：%s】", userId, username);
        logService.addNewOperationLog(LocalDateTime.now(), message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUser(AddOrUpdateUserQuery query) {
        Integer userId = query.getUserId();

        User userToUpdate = getUserFromDbMustBeExists(userId);
        String username = userToUpdate.getUsername();

        List<String> logs = new ArrayList<>();
        boolean needsUpdate = needsUpdateUser(userToUpdate, query, logs);
        if (!needsUpdate) {
            return;
        }

        int updatedNum = userMapper.update(userToUpdate);
        if (updatedNum <= 0) {
            throw new InternalException("用户修改失败");
        }

        String message = String.format("修改用户数据【ID：%s，用户名：%s】：%s", userId, username, String.join("；", logs));
        logService.addNewOperationLog(LocalDateTime.now(), message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUserPassword(UpdatePasswordQuery query) {
        Integer userId = query.getUserId();

        User passwordToUpdate = getUserFromDbMustBeExists(userId);
        String username = passwordToUpdate.getUsername();
        String hashedPassword = passwordToUpdate.getHashedPassword();

        verifyOldPassword(query.getOldPassword(), hashedPassword);

        verifyNewPasswordIsChanged(query.getNewPassword(), hashedPassword);

        int updatedNum = updateUserPasswordInDatabase(query);
        if (updatedNum <= 0) {
            throw new InternalException("密码修改失败");
        }

        String message = String.format("修改用户密码【ID：%s，用户名：%s】", userId, username);
        logService.addNewOperationLog(LocalDateTime.now(), message);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(int userId) {
        User userToDelete = getUserFromDbMustBeExists(userId);
        String username = userToDelete.getUsername();

        int deletedNum = userMapper.deleteById(userId);
        if (deletedNum <= 0) {
            throw new InternalException("用户删除失败");
        }

        String message = String.format("删除用户数据【ID：%s，用户名：%s】", userId, username);
        logService.addNewOperationLog(LocalDateTime.now(), message);
    }

    private int updateUserPasswordInDatabase(UpdatePasswordQuery query) {
        String rawPassword = query.getNewPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);

        User user = new User();

        user.setUserId(query.getUserId());
        user.setHashedPassword(hashedPassword);

        return userMapper.update(user);
    }

    private void verifyNewPasswordIsChanged(String newPassword, String hashedPassword) {
        boolean isMatched = passwordEncoder.matches(newPassword, hashedPassword);
        if (isMatched) {
            throw new BadRequestException("新密码不能与原密码相同");
        }
    }

    private void verifyOldPassword(String oldPassword, String hashedPassword) {
        boolean isMatched = passwordEncoder.matches(oldPassword, hashedPassword);
        if (!isMatched) {
            throw new BadRequestException("旧密码错误");
        }
    }

    private boolean needsUpdateUser(User userToUpdate, AddOrUpdateUserQuery query, List<String> logs) {
        boolean needsUpdatePassword = needsUpdatePassword(userToUpdate, query, logs);

        boolean needsUpdateRoles = needsUpdateRoles(userToUpdate, query, logs);

        boolean needsUpdateEnabled = needsUpdateEnabled(userToUpdate, query, logs);

        return needsUpdatePassword || needsUpdateRoles || needsUpdateEnabled;
    }

    private boolean needsUpdateEnabled(User userToUpdate, AddOrUpdateUserQuery query, List<String> logs) {
        YesOrNo newEnabled = YesOrNo.resolve(query.getEnabled()).orElse(null);
        if (newEnabled == null) {
            return false;
        }

        YesOrNo oldEnabled = userToUpdate.getEnabled();
        boolean isSame = newEnabled == oldEnabled;

        if (isSame) {
            userToUpdate.setEnabled(null);
            return false;
        } else {
            logs.add(String.format("将启用状态【%s】修改为【%s】", oldEnabled.name(), newEnabled.name()));

            userToUpdate.setEnabled(newEnabled);
            return true;
        }
    }

    private boolean needsUpdateRoles(User userToUpdate, AddOrUpdateUserQuery query, List<String> logs) {
        String newRoles = query.getRoles();
        if (newRoles == null) {
            return false;
        }

        String oldRoles = userToUpdate.getRoles();
        boolean isSame = StringUtils.equalsIgnoreBlank(newRoles, oldRoles);
        if (isSame) {
            userToUpdate.setRoles(null);
            return false;
        } else {
            logs.add(String.format("将角色【%s】修改为【%s】", oldRoles, newRoles));

            userToUpdate.setRoles(newRoles);
            return true;
        }
    }

    private boolean needsUpdatePassword(User userToUpdate, AddOrUpdateUserQuery query, List<String> logs) {
        String newRawPassword = query.getPassword();
        if (newRawPassword == null) {
            return false;
        }

        String oldHashedPassword = userToUpdate.getHashedPassword();
        boolean isSame = passwordEncoder.matches(newRawPassword, oldHashedPassword);
        if (isSame) {
            userToUpdate.setHashedPassword(null);
            return false;
        } else {
            logs.add("重置密码");

            String newHashedPassword = passwordEncoder.encode(newRawPassword);
            userToUpdate.setHashedPassword(newHashedPassword);
            return true;
        }
    }

    private User getUserFromDbMustBeExists(int userId) {
        return Optional.ofNullable(userMapper.findById(userId))
                .orElseThrow(() -> new NotFoundException(String.format("用户 ID【%s】不存在", userId)));
    }

    private User createUserToAdd(AddOrUpdateUserQuery query) {
        User userToAdd = new User();
        BeanUtil.copyProperties(query, userToAdd, "enabled");
        userToAdd.setHashedPassword(passwordEncoder.encode(query.getPassword()));
        userToAdd.setEnabled(YesOrNo.resolve(query.getEnabled()).orElseThrow());
        return userToAdd;
    }

    private void verifyUsernameUniqueness(String username) {
        boolean isExists = userMapper.existsUsername(username);
        if (isExists) {
            throw new ConflictException(String.format("用户名【%s】已存在", username));
        }
    }
}
