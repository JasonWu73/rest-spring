package net.wuxianjie.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

    /**
     * Token 的有效期，单位秒
     */
    private int expiresIn;

    /**
     * 用于访问 API 的 Token，即用于请求头 {@code Authorization: Bearer accessToken}
     */
    private String accessToken;

    /**
     * 用于刷新的 Token
     */
    private String refreshToken;
}
