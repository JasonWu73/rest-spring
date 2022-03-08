package net.wuxianjie.core.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "core.security")
public class SecurityConfigProperties {

    private String jwtSigningKey;

    /**
     * 允许任何人访问的请求路径，多个路径以英文逗号分隔，支持 AntPathMatcher 的匹配模式
     */
    private String permitAllAntPatterns;
}
