package net.wuxianjie.core.rest.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthConfigDto {

    /**
     * 用于生成与校验 JWT 的签名密钥
     */
    private String jwtSigningKey;

    /**
     * 开放访问的请求路径， 支持 {@code AntPathRequestMatcher} 模式，
     * 多个路径以英文逗号（{@code ,}）分隔
     */
    private String allowedAntPaths;
}
