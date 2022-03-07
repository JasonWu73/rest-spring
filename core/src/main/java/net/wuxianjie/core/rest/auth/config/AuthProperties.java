package net.wuxianjie.core.rest.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "core.auth")
public class AuthProperties {

    /**
     * 默认开放访问的请求路径
     */
    public static final String ALLOWED_ANT_PATHS = "/public/**";

    /**
     * 用于生成与验证 JWT 的签名密钥
     */
    private String jwtSigningKey;

    /**
     * 任何人都可访问的请求路径， 支持 AntPathRequestMatcher 模式，
     * 多个路径以英文逗号（,）分隔。
     *
     * <p>默认放开访问认证的请求路径为：/public/**</p>
     */
    private String allowedAntPaths = ALLOWED_ANT_PATHS;
}
