package net.wuxianjie.springbootcore.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * Access Token 管理。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    /**
     * 获取 Access Token。
     */
    @PostMapping("access_token")
    public TokenData getToken(@RequestBody @Validated GetTokenQuery query) {
        return tokenService.getToken(query.getAccountName(), query.getAccountPassword());
    }

    /**
     * 刷新 Access Token。
     */
    @GetMapping("refresh_token/{refreshToken}")
    public TokenData refreshToken(@PathVariable String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }

    @Data
    private static class GetTokenQuery {

        /**
         * 账号名称。
         */
        @NotBlank(message = "账号名称不能为空")
        @Length(message = "账号名称最长不能超过 100 个字符", max = 100)
        private String accountName;

        /**
         * 账号密码。
         */
        @NotBlank(message = "账号密码不能为空")
        @Length(message = "账号密码最长不能超过 100 个字符", max = 100)
        private String accountPassword;
    }
}
