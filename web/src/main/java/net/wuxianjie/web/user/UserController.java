package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingData;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.security.Admin;
import net.wuxianjie.springbootcore.security.AuthenticationFacade;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.BadRequestException;
import net.wuxianjie.springbootcore.shared.StringUtils;
import net.wuxianjie.springbootcore.shared.Wrote2Db;
import net.wuxianjie.springbootcore.validator.group.Add;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 用户管理。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    /**
     * 获取用户列表。
     */
    @Admin
    @GetMapping("list")
    public PagingData<List<UserListItemDto>> getUsers(@Validated PagingQuery paging, @Validated GetUserQuery query) {
        setFuzzySearchValue(query);

        return userService.getUsers(paging, query);
    }

    /**
     * 新增用户。
     */
    @Admin
    @PostMapping("add")
    public Wrote2Db addNewUser(@RequestBody @Validated(Add.class) AddOrUpdateUserQuery query) {
        setRoleAfterDeduplication(query);

        return userService.addNewUser(query);
    }

    /**
     * 修改用户。
     *
     * <p>注意：此处重置密码无需校验旧密码。</p>
     */
    @Admin
    @PostMapping("update/{userId:\\d+}")
    public Wrote2Db updateUser(@PathVariable("userId") int id, @RequestBody @Validated AddOrUpdateUserQuery query) {
        query.setUserId(id);

        setRoleAfterDeduplication(query);

        return userService.updateUser(query);
    }

    /**
     * 修改当前用户密码。
     */
    @PostMapping("password")
    public Wrote2Db updateCurrentUserPassword(@RequestBody @Validated UpdatePasswordQuery query) {
        validatePasswordDifference(query.getOldPassword(), query.getNewPassword());

        setCurrentUserId(query);

        return userService.updateUserPassword(query);
    }

    /**
     * 删除用户。
     */
    @Admin
    @GetMapping("del/{userId:\\d+}")
    public Wrote2Db deleteUser(@PathVariable("userId") int id) {
        return userService.deleteUser(id);
    }

    private void setCurrentUserId(UpdatePasswordQuery query) {
        TokenUserDetails userDetails = authenticationFacade.getCurrentUser();
        query.setUserId(userDetails.getAccountId());
    }

    private void validatePasswordDifference(String oldPassword, String newPassword) {
        if (Objects.equals(oldPassword, newPassword)) {
            throw new BadRequestException("新旧密码不能相同");
        }
    }

    private void setRoleAfterDeduplication(AddOrUpdateUserQuery query) {
        toDeduplicatedCommaSeparatedLowerCase(query.getRoles())
                .ifPresent(roleStr -> {
                    validateRole(roleStr);

                    query.setRoles(roleStr);
                });
    }

    private void validateRole(String commaSeparatedRole) {
        if (StrUtil.isEmpty(commaSeparatedRole)) {
            return;
        }

        String[] roles = commaSeparatedRole.split(",");
        boolean hasInvalidRole = Arrays.stream(roles)
                .anyMatch(x -> Role.resolve(x).isEmpty());
        if (hasInvalidRole) {
            throw new BadRequestException("包含未定义角色");
        }
    }

    private Optional<String> toDeduplicatedCommaSeparatedLowerCase(String commaSeparatedRoles) {
        if (StrUtil.isEmpty(commaSeparatedRoles)) {
            return Optional.empty();
        }

        return Optional.of(Arrays.stream(commaSeparatedRoles.split(","))
                .reduce("", (roleOne, roleTwo) -> {
                    String trimmedLowerCaseRole = roleTwo.trim().toLowerCase();
                    if (roleOne.contains(trimmedLowerCaseRole)) {
                        return roleOne;
                    }
                    if (StrUtil.isEmpty(roleOne)) {
                        return trimmedLowerCaseRole;
                    }
                    return roleOne + "," + trimmedLowerCaseRole;
                })
        );
    }

    private void setFuzzySearchValue(GetUserQuery query) {
        query.setUsername(StringUtils.generateDbFuzzyStr(query.getUsername()));
    }
}
