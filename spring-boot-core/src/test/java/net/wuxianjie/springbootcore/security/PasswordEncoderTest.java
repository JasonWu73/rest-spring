package net.wuxianjie.springbootcore.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@Slf4j
@SpringBootTest(classes = SecurityConfig.class)
class PasswordEncoderTest {

    private String rawPassword;
    private static String hashedPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        rawPassword = "123";

        hashedPassword = passwordEncoder.encode(rawPassword);

        log.info("原密码：{}，编码后为：{}", rawPassword, hashedPassword);
    }

    @Test
    @DisplayName("编码明文密码")
    void canGetHashedPassword() {
        // when
        hashedPassword = passwordEncoder.encode(rawPassword);

        // then
        assertThat(hashedPassword).isNotNull();
    }

    @Test
    @DisplayName("校验哈希密码是否匹配")
    void itShouldCheckMatch() {
        // when
        boolean actual = passwordEncoder.matches(rawPassword, hashedPassword);

        // then
        assertThat(actual).isTrue();
    }
}