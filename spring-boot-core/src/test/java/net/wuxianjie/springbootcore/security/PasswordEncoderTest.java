package net.wuxianjie.springbootcore.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试哈希密码的生成和匹配。
 *
 * @author 吴仙杰
 */
@Slf4j
@SpringBootTest(classes = SecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordEncoderTest {

    private static final String RAW_PASSWORD = "123";

    private static String hashedPassword;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Order(1)
    void encodePasswordShouldNotReturnNull() {
        hashedPassword = passwordEncoder.encode(RAW_PASSWORD);

        assertNotNull(hashedPassword);

        log.info("原密码：{}，编码后为：{}", RAW_PASSWORD, hashedPassword);
    }

    @Test
    @Order(2)
    void rawAndEncodedPasswordShouldEqual() {
        boolean isMatched = passwordEncoder.matches(RAW_PASSWORD, hashedPassword);
        assertTrue(isMatched);
    }
}