package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.paging.PagingData;
import net.wuxianjie.core.paging.PagingQuery;
import net.wuxianjie.core.security.Admin;
import net.wuxianjie.core.security.AuthenticationFacade;
import net.wuxianjie.core.security.Role;
import net.wuxianjie.core.security.TokenUserDetails;
import net.wuxianjie.core.shared.BadRequestException;
import net.wuxianjie.core.shared.Wrote2Db;
import net.wuxianjie.core.util.StrUtils;
import net.wuxianjie.core.validator.group.Add;
import net.wuxianjie.core.validator.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 用户管理。
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    /**
     * 获取用户列表。
     */
    @Admin
    @GetMapping("list")
    public PagingData<List<ManagementOfUser>> getUsers(@Validated PagingQuery paging,
                                                       @Validated ManagementOfUser query) {
        final String fuzzyUsername = StrUtils.generateDbFuzzyStr(query.getUsername());

        query.setUsername(fuzzyUsername);

        return userService.getUsers(paging, query);
    }

    /**
     * 新增用户。
     */
    @Admin
    @PostMapping("add")
    public Wrote2Db addNewUser(@RequestBody @Validated(Add.class) ManagementOfUser query) {
        final String originalRoleStr = query.getRoles();
        final String commaSeparatedLowerCaseRoleStr = toDeduplicateCommaSeparatedLowerCaseStr(originalRoleStr);

        validateRoleStr(commaSeparatedLowerCaseRoleStr);

        query.setRoles(commaSeparatedLowerCaseRoleStr);

        return userService.addNewUser(query);
    }

    /**
     * 修改用户。
     *
     * <p>注意：此处重置密码无需校验旧密码。</p>
     */
    @Admin
    @PostMapping("update/{userId:\\d+}")
    public Wrote2Db updateUser(@PathVariable("userId") int id,
                               @RequestBody @Validated ManagementOfUser query) {
        query.setUserId(id);

        final String originalRoleStr = query.getRoles();
        final String commaSeparatedLowerCaseRoleStr = toDeduplicateCommaSeparatedLowerCaseStr(originalRoleStr);

        validateRoleStr(commaSeparatedLowerCaseRoleStr);

        query.setRoles(commaSeparatedLowerCaseRoleStr);

        return userService.updateUser(query);
    }

    /**
     * 修改当前用户密码。
     */
    @PostMapping("password")
    public Wrote2Db updateCurrentUserPassword(
            @RequestBody @Validated(Update.class) ManagementOfUser query
    ) {
        final TokenUserDetails userDetails = authenticationFacade.getCurrentLoggedInUserDetails();

        query.setUserId(userDetails.getAccountId());

        if (Objects.equals(query.getOldPassword(), query.getNewPassword())) {
            throw new BadRequestException("新旧密码不能相同");
        }

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

    @Nullable
    private String toDeduplicateCommaSeparatedLowerCaseStr(String roles) {
        if (StrUtil.isEmpty(roles)) {
            return roles;
        }

        return Arrays.stream(roles.split(","))
                .reduce("", (s1, s2) -> {
                    final String trimmedLowerCaseStr = s2.trim().toLowerCase();

                    if (s1.contains(trimmedLowerCaseStr)) {
                        return s1;
                    }

                    if (StrUtil.isNotEmpty(s1)) {
                        return s1 + "," + trimmedLowerCaseStr;
                    }

                    return trimmedLowerCaseStr;
                });
    }

    private void validateRoleStr(String commaSeparatedLowerCaseRoleStr) {
        if (StrUtil.isEmpty(commaSeparatedLowerCaseRoleStr)) {
            return;
        }

        final boolean hasInvalidRole = Arrays.stream(commaSeparatedLowerCaseRoleStr.split(","))
                .anyMatch(x -> Role.resolve(x) == null);

        if (hasInvalidRole) {
            throw new BadRequestException("包含未定义角色");
        }
    }
}