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
@Slf4j
@SpringBootTest(classes = WebSecurityConfig.class)
class PasswordEncoderTest {

  @Autowired
  private PasswordEncoder underTest;

  @MockBean
  private ObjectMapper objectMapper;

  @MockBean
  private SecurityConfig securityConfig;

  @MockBean
  private TokenAuthenticationFilter authFilter;

  private String rawPassword;
  private String hashedPassword;

  @BeforeEach
  void setUp() {
    rawPassword = "123";
    hashedPassword = underTest.encode(rawPassword);

    log.info("原密码：{}，编码后为：{}", rawPassword, hashedPassword);
  }

  @Test
  @DisplayName("编码明文密码")
  void canGetHashedPassword() {
    // given
    // when
    hashedPassword = underTest.encode(rawPassword);

    // then
    assertThat(hashedPassword).isNotNull();
  }

  @Test
  @DisplayName("验证密码：匹配")
  void testMatchPassword() {
    // given
    // when
    boolean actual = underTest.matches(rawPassword, hashedPassword);

    // then
    assertThat(actual).isTrue();
  }

  @Test
  @DisplayName("验证密码：不匹配")
  void testNotMatch() {
    // given
    rawPassword = "234";

    // when
    boolean actual = underTest.matches(rawPassword, hashedPassword);

    // then
    assertThat(actual).isFalse();
  }
}