package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.operationlog.OperationLogger;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.Admin;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.shared.AuthUtils;
import net.wuxianjie.springbootcore.shared.exception.BadRequestException;
import net.wuxianjie.springbootcore.shared.util.StringUtils;
import net.wuxianjie.springbootcore.validator.group.GroupOne;
import net.wuxianjie.springbootcore.validator.group.GroupTwo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Optional;

/**
 * 用户管理控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表。
     *
     * @param paging 分页参数
     * @param query  查询参数
     * @return 用户列表
     */
    @Admin
    @GetMapping("list")
    public PagingResult<UserDto> getUsers(@Validated final PagingQuery paging,
                                          @Validated final UserQuery query) {
        setFuzzySearchValue(query);

        return userService.getUsers(paging, query);
    }

    /**
     * 新增用户。
     *
     * @param query 查询参数
     */
    @Admin
    @OperationLogger("新增用户")
    @PostMapping("add")
    public void saveUser(@RequestBody @Validated(GroupOne.class) final UserQuery query) {
        setRoleAfterDeduplication(query);

        userService.saveUser(query);
    }

    /**
     * 修改用户。
     * <p>
     * 注意：此处为重置密码，即无需验证旧密码。
     * </p>
     *
     * @param id    用户 id
     * @param query 查询参数
     */
    @Admin
    @OperationLogger("修改用户")
    @PostMapping("update/{userId:\\d+}")
    public void updateUser(@PathVariable("userId") final int id,
                           @RequestBody @Validated final UserQuery query) {
        query.setUserId(id);

        setRoleAfterDeduplication(query);

        userService.updateUser(query);
    }

    /**
     * 修改当前用户密码。
     *
     * @param query 查询参数
     */
    @PostMapping("password")
    public void updatePassword(@RequestBody @Validated(GroupTwo.class) final UserQuery query) {
        verifyPasswordDifference(query.getOldPassword(), query.getNewPassword());

        setCurrentUserId(query);

        userService.updatePassword(query);
    }

    /**
     * 删除用户。
     *
     * @param id 用户 id
     */
    @Admin
    @OperationLogger("删除用户")
    @GetMapping("del/{userId:\\d+}")
    public void removeUser(@PathVariable("userId") final int id) {
        userService.removeUser(id);
    }

    private void setFuzzySearchValue(final UserQuery query) {
        query.setUsername(StringUtils.getFuzzySearchValue(query.getUsername()));
    }

    private void setRoleAfterDeduplication(final UserQuery query) {
        toDeduplicatedCommaSeparatedLowerCase(query.getRoles())
                .ifPresent(roleStr -> {
                    verifyRole(roleStr);

                    query.setRoles(roleStr);
                });
    }

    private Optional<String> toDeduplicatedCommaSeparatedLowerCase(final String commaSeparatedRoles) {
        if (StrUtil.isEmpty(commaSeparatedRoles)) return Optional.empty();

        return Optional.of(Arrays.stream(commaSeparatedRoles.split(","))
                .reduce("", (roleOne, roleTwo) -> {
                    final String appended = roleTwo.trim().toLowerCase();

                    if (roleOne.contains(appended)) return roleOne;

                    if (StrUtil.isEmpty(roleOne)) return appended;

                    return roleOne + "," + appended;
                })
        );
    }

    private void verifyRole(final String commaSeparatedRole) {
        if (StrUtil.isEmpty(commaSeparatedRole)) return;

        final boolean hasInvalidRole = Arrays.stream(commaSeparatedRole.split(","))
                .anyMatch(x -> Role.resolve(x).isEmpty());
        if (hasInvalidRole) throw new BadRequestException("包含未定义角色");
    }

    private void verifyPasswordDifference(final String oldPassword, final String newPassword) {
        if (StrUtil.equals(oldPassword, newPassword)) throw new BadRequestException("新旧密码不能相同");
    }

    private void setCurrentUserId(final UserQuery query) {
        final UserDetails userDetails = (UserDetails) AuthUtils.getCurrentUser().orElseThrow();
        query.setUserId(userDetails.getAccountId());
    }
}
