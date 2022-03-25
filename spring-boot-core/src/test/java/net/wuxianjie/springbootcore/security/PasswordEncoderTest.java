package net.wuxianjie.springbootcore.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 吴仙杰
 */
@SpringBootTest(classes = {SecurityConfig.class})
@Slf4j
class PasswordEncoderTest {

    private String rawPassword;
    private String hashedPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init() {
        rawPassword = "123";
        hashedPassword = passwordEncoder.encode(rawPassword);

        log.info("原密码：{}，编码后为：{}", rawPassword, hashedPassword);
    }

    @Test
    void encodePasswordShouldNotReturnNull() {
        assertNotNull(hashedPassword);
    }

    @Test
    void rawAndEncodedPasswordShouldMatch() {
        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword));
    }
}