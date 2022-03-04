package net.wuxianjie.core.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.dto.TokenDto;
import net.wuxianjie.core.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenController {

    private final TokenService tokenService;

    /**
     * 获取 Access Token
     *
     * @param query 请求参数
     * @return Token
     */
    @PostMapping("/access_token")
    public TokenDto getToken(@RequestBody @Validated final Query query) {
        return tokenService.getToken(query.getAccountName(), query.getAccountPassword());
    }

    /**
     * 刷新 Access Token
     *
     * @param refreshToken 用于刷新的 Refresh Token，必填
     * @return Token
     */
    @GetMapping("/refresh_token/{refreshToken}")
    public TokenDto updateToken(@PathVariable final String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }

    @Data
    private static class Query {

        /**
         * 账号名称，必填
         */
        @NotBlank(message = "账号不能为空")
        private String accountName;

        /**
         * 账号密码，必填
         */
        @NotBlank(message = "密码不能为空")
        private String accountPassword;
    }
}
