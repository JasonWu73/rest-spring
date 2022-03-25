package net.wuxianjie.springbootcore.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Web 安全相关的配置项属性说明。
 *
 * @author 吴仙杰
 * @see SecurityConfigData
 * @see WebSecurityConfig
 */
@Configuration
@ConfigurationProperties(prefix = "core.security")
@Getter
@Setter
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
