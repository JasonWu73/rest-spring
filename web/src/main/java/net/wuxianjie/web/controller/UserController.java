package net.wuxianjie.web.controller;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.constant.Role;
import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.core.dto.PrincipalDto;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.service.AuthenticationFacade;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.core.validator.group.Save;
import net.wuxianjie.core.validator.group.Update;
import net.wuxianjie.web.dto.UserDto;
import net.wuxianjie.web.dto.Wrote2DbDto;
import net.wuxianjie.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    private final AuthenticationFacade authentication;

    /**
     * 获取用户分页数据
     *
     * @param pagination 分页条件
     * @param username   用户名，空或不传都代表不使用该查询条件
     * @return 用户分页数据
     */
    @Admin
    @GetMapping("list")
    public PaginationDto<List<UserDto>> getUsers(
            @Valid final PaginationQueryDto pagination,
            final String username
    ) {
        // 获取模糊查询参数
        final String fuzzyUsername = StringUtils.generateDbFuzzyStr(username);

        // 获取分页数据
        return userService.getUsers(pagination, fuzzyUsername);
    }

    /**
     * 新增用户
     *
     * @param userToAdd 需要新增的用户数据
     * @return Wrote2DbDto
     */
    @Admin
    @PostMapping("add")
    public Wrote2DbDto saveUser(@RequestBody @Validated(Save.class) final UserDto userToAdd) {
        // 处理角色字符串：去重、转小写，再以英文逗号作分隔符拼接为符合要求的角色字符串
        final String roles = toDeduplicateLowerCaseCommaSeparatedRoles(userToAdd.getRoles());
        userToAdd.setRoles(roles);

        // 检查角色是否都合法
        validateRoles(roles);

        // 保存用户
        return userService.saveUser(userToAdd);
    }

    /**
     * 更新用户
     *
     * <p>注意：此处重置密码无需校验旧密码</p>
     *
     * @param userId       需要更新的用户 ID，必填
     * @param userToUpdate 用户的最新数据
     * @return Wrote2DbDto
     */
    @Admin
    @PostMapping("update/{userId:\\d+}")
    public Wrote2DbDto updateUser(
            @PathVariable final int userId,
            @RequestBody @Validated final UserDto userToUpdate
    ) {
        // 完善更新参数
        userToUpdate.setUserId(userId);

        // 处理角色字符串：去重、转小写，再以英文逗号作分隔符拼接为符合要求的角色字符串
        final String roleStr = toDeduplicateLowerCaseCommaSeparatedRoles(userToUpdate.getRoles());
        userToUpdate.setRoles(roleStr);

        // 检查角色是否都合法
        validateRoles(roleStr);

        // 更新用户
        return userService.updateUser(userToUpdate);
    }

    /**
     * 修改当前用户密码
     *
     * @param passwordToUpdate 需要更新的密码
     * @return Wrote2DbDto
     */
    @PostMapping("password")
    public Wrote2DbDto updatePassword(
            @RequestBody @Validated(Update.class) final UserDto passwordToUpdate
    ) {
        // 获取当前登录用户
        final PrincipalDto principal = authentication.getPrincipal();

        // 完善修改密码参数
        passwordToUpdate.setUserId(principal.getAccountId());

        // 检查传入的新旧密码是否相同
        if (passwordToUpdate.getOldPassword().equals(passwordToUpdate.getNewPassword())) {
            throw new BadRequestException("新旧密码不能相同");
        }

        // 修改用户密码
        return userService.updatePassword(passwordToUpdate);
    }

    /**
     * 删除用户
     *
     * @param userId 需要删除的用户 ID，必填
     * @return Wrote2DbDto
     */
    @Admin
    @GetMapping("del/{userId:\\d+}")
    public Wrote2DbDto removeUser(@PathVariable final int userId) {
        return userService.removeUser(userId);
    }

    private void validateRoles(final String roles) {
        if (StrUtil.isEmpty(roles)) {
            return;
        }

        final boolean hasInvalidRole = Arrays.stream(roles.split(","))
                .anyMatch(x -> Role.resolve(x) == null);

        if (hasInvalidRole) {
            throw new BadRequestException("包含未定义角色");
        }
    }

    private String toDeduplicateLowerCaseCommaSeparatedRoles(final String roles) {
        if (StrUtil.isEmpty(roles)) {
            return roles;
        }

        return Arrays.stream(roles.split(","))
                .reduce("", (s, s2) -> {
                    final String appended = s2.trim().toLowerCase();

                    if (s.contains(appended)) {
                        return s;
                    }

                    if (StrUtil.isNotEmpty(s)) {
                        return s + "," + appended;
                    }

                    return appended;
                });
    }
}
