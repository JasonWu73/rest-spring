package net.wuxianjie.web.user;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.shared.exception.BadRequestException;
import net.wuxianjie.springbootcore.shared.exception.DataConflictException;
import net.wuxianjie.springbootcore.shared.exception.NotFoundException;
import net.wuxianjie.springbootcore.shared.util.StringUtils;
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
    public PagingResult<UserDto> getUsers(final PagingQuery paging, final UserQuery query) {
        final List<UserDto> users = userMapper.selectUsers(paging, query);

        final int total = userMapper.countUsers(query);

        return new PagingResult<>(paging, total, users);
    }

    /**
     * 新增用户。
     *
     * @param query 查询参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveUser(final UserQuery query) {
        verifyUsernameUniqueness(query.getUsername());

        final User user = buildUser(query);

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
    public void updateUser(final UserQuery query) {
        final User user = getUserFromDbMustBeExists(query.getUserId());

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
    public void updateUserPassword(final UserQuery query) {
        final User user = getUserFromDbMustBeExists(query.getUserId());

        verifyPassword(query.getOldPassword(), user.getHashedPassword());

        final String rawPassword = query.getNewPassword();
        final String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setHashedPassword(hashedPassword);

        userMapper.updateUser(user);
    }

    /**
     * 删除用户。
     *
     * @param userId 用户 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(int userId) {
        userMapper.deleteUserById(userId);
    }

    private void verifyUsernameUniqueness(final String username) {
        boolean isExists = userMapper.existsUserByName(username);
        if (isExists) throw new DataConflictException("已存在相同用户名");
    }

    private User buildUser(final UserQuery query) {
        final User user = new User();

        BeanUtil.copyProperties(query, user, "enabled");

        user.setHashedPassword(passwordEncoder.encode(query.getPassword()));
        user.setEnabled(YesOrNo.resolve(query.getEnabled()).orElseThrow());

        return user;
    }

    private User getUserFromDbMustBeExists(final int userId) {
        return Optional.ofNullable(userMapper.selectUserById(userId))
                .orElseThrow(() -> new NotFoundException("用户 ID 不存在"));
    }

    private boolean needsUpdateUser(final User user, final UserQuery query) {
        boolean needsUpdate = false;

        final String newPassword = query.getPassword();
        if (newPassword != null && !passwordEncoder.matches(newPassword, user.getHashedPassword())) {
            needsUpdate = true;
            user.setHashedPassword(passwordEncoder.encode(newPassword));
        }

        final String newRoles = query.getRoles();
        if (newRoles != null && StringUtils.equalsIgnoreBlank(newRoles, user.getRoles())) {
            needsUpdate = true;
            user.setRoles(newRoles);
        }

        final YesOrNo newEnabled = YesOrNo.resolve(query.getEnabled()).orElse(null);
        if (newEnabled != null && newEnabled == user.getEnabled()) {
            needsUpdate = true;
            user.setEnabled(newEnabled);
        }

        return needsUpdate;
    }

    private void verifyPassword(final String oldPassword, final String hashedPassword) {
        final boolean isMatched = passwordEncoder.matches(oldPassword, hashedPassword);
        if (!isMatched) throw new BadRequestException("旧密码错误");
    }
}
