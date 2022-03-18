package net.wuxianjie.springbootcore.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 实现 Token 认证机制所需的配置属性说明。
 *
 * @author 吴仙杰
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "core.security")
public class SecurityConfigProperties {

    /**
     * JWT 签名密钥。
     */
    private String jwtSigningKey;

    /**
     * 无需认证即可访问的请求路径，多个路径以英文逗号分隔，支持 AntPathMatcher 的匹配模式。
     */
    private String permitAllAntPatterns;
}
