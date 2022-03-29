package net.wuxianjie.springbootcore.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/auth-test")
@RequiredArgsConstructor
public class AuthController {

    /**
     * 开放访问的 API，即无需 Access Token 也可访问。
     */
    @GetMapping("public")
    public Result testPublic() {
        return new Result(
                "无需 Token 认证即可访问的开放 API",
                getUsername()
        );
    }

    /**
     * 只要通过 Token 认证就可访问，即未绑定角色的 Token 也可访问。
     */
    @GetMapping("authenticated")
    public Result testAuthenticated() {
        return new Result(
                "只要通过 Token 认证（登录后）即可访问的 API",
                getUsername()
        );
    }

    /**
     * 通过 Token 认证，且必须拥有 user 角色才能访问。
     */
    @User
    @GetMapping("user")
    public Result testUserRole() {
        return getResult(Role.USER);
    }

    /**
     * 通过 Token 认证，且必须拥有 admin 角色才能访问。
     */
    @Admin
    @GetMapping("admin")
    public Result testAdminRole() {
        return getResult(Role.ADMIN);
    }

    /**
     * 通过 Token 认证，且必须拥有 user 或 admin 角色才能访问。
     */
    @UserOrAdmin
    @GetMapping("user-or-admin")
    public Result testUserOrAdmin() {
        String msg = String.format(
                "通过 Token 认证且必须拥有 [%s] 或 [%s] 角色才可访问的 API",
                Role.USER.value(),
                Role.ADMIN.value()
        );

        return new Result(msg, getUsername());
    }

    private Result getResult(Role role) {
        String msg = String.format(
                "通过 Token 认证且必须拥有 [%s] 角色才可访问的 API",
                role.value()
        );

        return new Result(msg, getUsername());
    }

    private String getUsername() {
        return AuthUtils.getLoggedIn()
                .map(TokenDetails::getAccountName)
                .orElse("匿名用户");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Result {

        private String message;
        private String username;
    }
}
