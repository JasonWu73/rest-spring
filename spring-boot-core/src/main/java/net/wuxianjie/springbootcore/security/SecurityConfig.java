package net.wuxianjie.springbootcore.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 安全相关配置类。
 *
 * @author 吴仙杰
 */
@Configuration
public class SecurityConfig {

  /**
   * 密码哈希编码器。
   *
   * @return {@link PasswordEncoder}
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
