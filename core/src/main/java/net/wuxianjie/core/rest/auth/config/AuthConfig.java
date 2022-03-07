package net.wuxianjie.core.rest.auth.config;

import net.wuxianjie.core.rest.auth.dto.AuthConfigDto;
import net.wuxianjie.core.shared.YamlPropertySourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@PropertySource(value = "classpath:core.yml",
        factory = YamlPropertySourceFactory.class
)
public class AuthConfig {

    @Value("${core.auth.jwt-signing-key}")
    private String jwtSigningKey;

    @Value("${core.auth.allowed-ant-paths}")
    private String allowedAntPaths;

    @Bean
    public AuthConfigDto authConfigDto() {
        return new AuthConfigDto(jwtSigningKey, allowedAntPaths);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
