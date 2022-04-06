package net.wuxianjie.springbootcore.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@SpringBootTest(classes = WebSecurityConfig.class)
@Slf4j
class PasswordEncoderTest {

    @SuppressWarnings("unused")
    @MockBean
    private ObjectMapper objectMapper;
    @SuppressWarnings("unused")
    @MockBean
    private SecurityConfig securityConfig;
    @SuppressWarnings("unused")
    @MockBean
    private TokenAuthenticationFilter authFilter;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String rawPassword;
    private static String hashedPassword;

    @BeforeEach
    void setUp() {
        rawPassword = "123";
        hashedPassword = passwordEncoder.encode(rawPassword);
        log.info("原密码：{}，编码后为：{}", rawPassword, hashedPassword);
    }

    @Test
    @DisplayName("编码明文密码")
    void canGetHashedPassword() {
        // given
        // when
        hashedPassword = passwordEncoder.encode(rawPassword);

        // then
        assertThat(hashedPassword).isNotNull();
    }

    @Test
    @DisplayName("哈希密码与明文密码匹配")
    void itShouldCheckMatch() {
        // given
        // when
        final boolean actual = passwordEncoder.matches(rawPassword, hashedPassword);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("哈希密码与明文密码不匹配")
    void itShouldCheckNotMatch() {
        // given
        rawPassword = "234";

        // when
        final boolean actual = passwordEncoder.matches(rawPassword, hashedPassword);

        // then
        assertThat(actual).isFalse();
    }
}