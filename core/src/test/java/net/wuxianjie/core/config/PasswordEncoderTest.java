package net.wuxianjie.core.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 测试密码编码。
 *
 * @author 吴仙杰
 */
@Slf4j
@SpringBootTest(classes = CoreConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PasswordEncoderTest {

  private static final String RAW_PASSWORD = "123";

  private static String encodedPassword = "$2a$10$9Tq5H9wCOiRg97zR5K.6ye.5TIAQUVhDPFhm5YsbvSgpJhwTj3.yW";

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  @Order(1)
  void encodePasswordShouldNotReturnNull() {
    encodedPassword = passwordEncoder.encode(RAW_PASSWORD);
    assertNotNull(encodedPassword);
    log.info("原密码: {}，编码后为: {}", RAW_PASSWORD, encodedPassword);
  }

  @Test
  @Order(2)
  void rawAndEncodedPasswordShouldEqual() {
    final boolean matched = passwordEncoder.matches(RAW_PASSWORD, encodedPassword);
    assertTrue(matched);
  }
}