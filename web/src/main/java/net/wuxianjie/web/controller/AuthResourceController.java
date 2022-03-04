package net.wuxianjie.web.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.annotation.User;
import net.wuxianjie.core.annotation.UserOrAdmin;
import net.wuxianjie.core.constant.Role;
import net.wuxianjie.core.service.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试 Token 认证是否有效
 */
@RestController
@RequestMapping("/auth-resource")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthResourceController {

    private final AuthenticationFacade authentication;

    /**
     * 游客（匿名用户）可访问，即无需身份认证即可访问
     */
    @GetMapping("public")
    public ResultDto testAnonymousResource() {
        return new ResultDto(
                "无需身份认证即可访问的开放API",
                authentication.getCacheToken().getAccountName()
        );
    }

    /**
     * 来宾（角色为空的用户），即只要通过身份认证即可访问
     */
    @GetMapping("guest")
    public ResultDto testGuestResource() {
        return new ResultDto(
                "只要通过身份认证即可访问的登录后可访问API",
                authentication.getCacheToken().getAccountName()
        );
    }

    /**
     * 通过身份认证，且必须拥有 user 角色才能访问
     */
    @User
    @GetMapping("user")
    public ResultDto getUserResource() {
        return getResult(Role.USER);
    }

    /**
     * 通过身份认证，且必须拥有 admin 角色才能访问
     */
    @Admin
    @GetMapping("admin")
    public ResultDto getAdminResource() {
        return getResult(Role.ADMIN);
    }

    /**
     * 通过身份认证，且必须拥有 user 或 admin 角色才能访问
     */
    @UserOrAdmin
    @GetMapping("user-or-admin")
    public ResultDto getUserOrAdmin() {
        return new ResultDto(
                String.format("通过身份认证且必须拥有【%s】或【%s】角色才可访问API",
                        Role.USER.value(),
                        Role.ADMIN.value()
                ),
                authentication.getCacheToken().getAccountName()
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ResultDto {
        private String message;
        private String username;
    }

    private ResultDto getResult(final Role role) {
        return new ResultDto(
                String.format("通过身份认证且必须拥有【%s】角色才可访问API", role.value()),
                authentication.getCacheToken().getAccountName()
        );
    }
}
