package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.paging.PagingData;
import net.wuxianjie.core.paging.PagingQuery;
import net.wuxianjie.core.security.Admin;
import net.wuxianjie.core.security.AuthenticationFacade;
import net.wuxianjie.core.security.Role;
import net.wuxianjie.core.security.TokenUserDetails;
import net.wuxianjie.core.shared.Written2Db;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.core.validator.group.Add;
import net.wuxianjie.core.validator.group.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    /**
     * 获取用户列表
     */
    @Admin
    @GetMapping("list")
    public PagingData<List<ManagementOfUser>> getUsers(@Validated PagingQuery paging, String username) {
        String fuzzyUsername = StringUtils.generateDbFuzzyStr(username);

        return userService.getUsers(paging, fuzzyUsername);
    }

    /**
     * 新增用户
     */
    @Admin
    @PostMapping("add")
    public Written2Db addNewUser(@RequestBody @Validated(Add.class) ManagementOfUser userToAdd) {
        String commaSeparatedLowerCaseRoleStr = toDeduplicateCommaSeparatedLowerCaseStr(userToAdd.getRoles());

        validateRoleStr(commaSeparatedLowerCaseRoleStr);

        userToAdd.setRoles(commaSeparatedLowerCaseRoleStr);

        return userService.addNewUser(userToAdd);
    }

    /**
     * 修改用户。
     *
     * <p>注意：此处重置密码无需校验旧密码</p>
     */
    @Admin
    @PostMapping("update/{userId:\\d+}")
    public Written2Db updateUser(
            @PathVariable("userId") int id,
            @RequestBody @Validated ManagementOfUser userToUpdate
    ) {
        userToUpdate.setUserId(id);

        String commaSeparatedLowerCaseRoleStr =
                toDeduplicateCommaSeparatedLowerCaseStr(userToUpdate.getRoles());

        validateRoleStr(commaSeparatedLowerCaseRoleStr);

        userToUpdate.setRoles(commaSeparatedLowerCaseRoleStr);

        return userService.updateUser(userToUpdate);
    }

    /**
     * 修改当前用户密码
     */
    @PostMapping("password")
    public Written2Db updateCurrentUserPassword(
            @RequestBody @Validated(Update.class) ManagementOfUser passwordToUpdate
    ) {
        TokenUserDetails userDetails = authenticationFacade.getCurrentLoggedInUserDetails();

        passwordToUpdate.setUserId(userDetails.getAccountId());

        if (passwordToUpdate.getOldPassword().equals(passwordToUpdate.getNewPassword())) {
            throw new BadRequestException("新旧密码不能相同");
        }

        return userService.updateUserPassword(passwordToUpdate);
    }

    /**
     * 删除用户
     */
    @Admin
    @GetMapping("del/{userId:\\d+}")
    public Written2Db deleteUser(@PathVariable("userId") int id) {
        return userService.removeUser(id);
    }

    private String toDeduplicateCommaSeparatedLowerCaseStr(String roles) {
        if (StrUtil.isEmpty(roles)) {
            return roles;
        }

        return Arrays.stream(roles.split(","))
                .reduce("", (s1, s2) -> {
                    String trimmedLowerCaseStr = s2.trim().toLowerCase();

                    if (s1.contains(trimmedLowerCaseStr)) {
                        return s1;
                    }

                    if (StrUtil.isNotEmpty(s1)) {
                        return s1 + "," + trimmedLowerCaseStr;
                    }

                    return trimmedLowerCaseStr;
                });
    }

    private void validateRoleStr(String roles) {
        if (StrUtil.isEmpty(roles)) {
            return;
        }

        boolean hasInvalidRole = Arrays.stream(roles.split(","))
                .anyMatch(x -> Role.resolve(x) == null);

        if (hasInvalidRole) {
            throw new BadRequestException("包含未定义角色");
        }
    }
}
