package net.wuxianjie.core.security;

import net.wuxianjie.core.shared.YamlSourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@PropertySource(value = "classpath:core.yml", factory = YamlSourceFactory.class)
public class SecurityConfig {

    @Value("${core.security.jwt-signing-key}")
    private String jwtSigningKey;

    @Value("${core.security.permit-all-ant-patterns}")
    private String permitAllAntPatterns;

    @Bean
    public SecurityConfigData securityConfigData() {
        return new SecurityConfigData(jwtSigningKey, permitAllAntPatterns);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
