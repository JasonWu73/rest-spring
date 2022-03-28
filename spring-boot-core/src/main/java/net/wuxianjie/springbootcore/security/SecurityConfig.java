package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import net.wuxianjie.springbootcore.shared.YamlSourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Web 安全相关所需配置。
 *
 * @author 吴仙杰
 */
@Configuration
@PropertySource(value = "classpath:core.yml", factory = YamlSourceFactory.class)
public class SecurityConfig {

    @Value("${core.security.jwt-signing-key}")
    private String jwtSigningKey;

    @Value("${core.security.permit-all-ant-patterns}")
    private String permitAllAntPatterns;


    /**
     * 密码哈希编码器。
     *
     * @return {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Web 安全相关的配置对象。
     *
     * @return {@link SecurityConfigData}
     */
    @Bean
    public SecurityConfigData securityConfigData() {
        jwtSigningKey = StrUtil.trimToNull(jwtSigningKey);
        permitAllAntPatterns = StrUtil.trimToNull(permitAllAntPatterns);

        if (jwtSigningKey == null) {
            throw new IllegalArgumentException("core.security.jwt-signing-key 不能为空");
        }

        return new SecurityConfigData(jwtSigningKey, permitAllAntPatterns);
    }
}
