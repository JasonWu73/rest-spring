package net.wuxianjie.web.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.DataConflictException;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.web.dto.UserDto;
import net.wuxianjie.web.dto.Wrote2DbDto;
import net.wuxianjie.web.mapper.UserMapper;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.service.OperationLogService;
import net.wuxianjie.web.service.UserService;
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
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final OperationLogService logService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto getUser(@NonNull final String username) {
        final User user = userMapper.findByUsername(username);
        return new UserDto(user);
    }

    @Override
    public PaginationDto<List<UserDto>> getUsers(
            @NonNull final PaginationQueryDto pagination,
            final String fuzzyUsername
    ) {
        // 获取分页数据
        final List<User> users = userMapper.findByPagination(pagination, fuzzyUsername);
        final int total = userMapper.countByUsername(fuzzyUsername);

        // 构造 DTO
        final List<UserDto> userList = users.stream()
                .map(UserDto::new)
                .collect(Collectors.toList());

        // 返回分页数据对象
        return new PaginationDto<>(total, pagination.getPageNo(), pagination.getPageSize(), userList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wrote2DbDto saveUser(@NonNull final UserDto userToAdd) {
        // 校验是否已存在同名用户
        final int count = userMapper.countByUsername(userToAdd.getUsername());

        if (count > 0) {
            throw new DataConflictException("已存在相同用户名");
        }

        // 将明文密码编码为哈希值
        final String hashedPassword = passwordEncoder.encode(userToAdd.getPassword());
        userToAdd.setPassword(hashedPassword);

        // 在数据库中添加用户数据
        final int addedNum = userMapper.add(userToAdd);

        // 记录操作日志
        final String logMessage = String.format("新增用户【%s】", userToAdd.getUsername());
        logService.saveOperationLog(LocalDateTime.now(), logMessage);

        // 返回入库结果
        return new Wrote2DbDto(addedNum, "新增用户成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wrote2DbDto updateUser(@NonNull final UserDto userToUpdate) {
        // 检查用户是否存在
        final User user = userMapper.findById(userToUpdate.getUserId());

        if (user == null) {
            throw new BadRequestException("用户 ID 不存在");
        }

        // 判断用户数据是否需要更新，记录更新日志，并将不需要更新的数据设置为 null
        final List<String> logs = new ArrayList<>();
        final boolean needsUpdate = needsUpdateUserInfo(user, userToUpdate, logs);

        if (needsUpdate) {
            // 更新数据库中的用户数据
            final int updatedNum = userMapper.update(userToUpdate);

            // 记录操作日志
            final String logMessage = String.format("更新用户信息【%s】", String.join("；", logs));
            logService.saveOperationLog(LocalDateTime.now(), logMessage);

            return new Wrote2DbDto(updatedNum, "更新用户成功");
        }

        return new Wrote2DbDto(0, "无需更新用户");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wrote2DbDto updatePassword(@NonNull final UserDto passwordToUpdate) {
        // 检查用户是否存在
        final User user = userMapper.findById(passwordToUpdate.getUserId());

        if (user == null) {
            throw new BadRequestException("用户ID不存在");
        }

        // 检查旧密码与库中的密码是否一致
        final boolean isCorrect = passwordEncoder
                .matches(passwordToUpdate.getOldPassword(), user.getHashedPassword());

        if (!isCorrect) {
            throw new BadRequestException("旧密码错误");
        }

        // 将明文新密码编码为哈希值
        final String hashedNewPassword = passwordEncoder.encode(passwordToUpdate.getNewPassword());

        // 更新数据库中的用户密码
        final int updatedNum = userMapper
                .updatePasswordById(passwordToUpdate.getUserId(), hashedNewPassword);

        // 记录操作日志
        final String logMessage = String.format("修改用户【%s】的密码", user.getUsername());
        logService.saveOperationLog(LocalDateTime.now(), logMessage);

        return new Wrote2DbDto(updatedNum, "修改密码成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wrote2DbDto removeUser(final int userId) {
        // 检查用户是否存在
        final User user = userMapper.findById(userId);

        if (user == null) {
            throw new BadRequestException("用户 ID 不存在");
        }

        // 删除数据库中的指定用户
        final int deletedNum = userMapper.deleteById(userId);

        // 记录操作日志
        final String logMessage = String.format("删除用户【%s】", user.getUsername());
        logService.saveOperationLog(LocalDateTime.now(), logMessage);

        return new Wrote2DbDto(deletedNum, "删除用户成功");
    }

    private boolean needsUpdateUserInfo(
            final User user,
            final UserDto userToUpdate,
            final List<String> logs
    ) {
        boolean isChange = false;
        // 判断密码是否需要更改
        final boolean isSamePassword = userToUpdate.getPassword() != null &&
                passwordEncoder.matches(userToUpdate.getPassword(), user.getHashedPassword());

        if (userToUpdate.getPassword() != null && !isSamePassword) {
            isChange = true;

            // 将明文密码编码为哈希值
            final String hashedPassword = passwordEncoder.encode(userToUpdate.getPassword());
            userToUpdate.setHashedPassword(hashedPassword);

            logs.add(String.format("重置用户【%s】的密码", user.getUsername()));
        } else if (userToUpdate.getPassword() != null) {
            userToUpdate.setPassword(null);
        }

        // 判断角色是否需要更改
        final boolean isSameRoles = StringUtils
                .isNullEquals(user.getRoles(), userToUpdate.getRoles());

        if (userToUpdate.getRoles() != null && !isSameRoles) {
            isChange = true;

            logs.add(
                    String.format("将角色从【%s】修改为【%s】",
                            user.getRoles(),
                            userToUpdate.getRoles()
                    )
            );
        } else if (userToUpdate.getRoles() != null) {
            userToUpdate.setRoles(null);
        }

        return isChange;
    }
}
