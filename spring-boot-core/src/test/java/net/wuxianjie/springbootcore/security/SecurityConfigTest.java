package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.YamlSourceFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(SecurityConfig.class)
@PropertySource(value = "classpath:security.yml", factory = YamlSourceFactory.class)
class SecurityConfigTest {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Autowired
    private SecurityConfig securityConfig;

    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void canGetSecurityConfig() {
        // given
        // when
        // then
        validator.validate(securityConfig.getJwtSigningKey());
        validator.validate(securityConfig.getPermitAllAntPatterns());

        assertThat(securityConfig.getJwtSigningKey()).isEqualTo("Qrjfl4tcKH9Yx5KROHQ8jQdRmJgbZWvy3v4hEoxzFq0=");
        assertThat(securityConfig.getPermitAllAntPatterns()).isEqualTo("/api/v1/auth-test/public, /public/**");
    }
}