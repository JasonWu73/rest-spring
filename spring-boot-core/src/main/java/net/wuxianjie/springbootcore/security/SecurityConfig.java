package net.wuxianjie.springbootcore.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * 安全配置项的配置类。
 *
 * @author 吴仙杰
 * @see WebSecurityConfig
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "core.security")
public class SecurityConfig {

  /**
   * JWT 签名密钥。
   */
  @NotBlank(message = "JWT 签名密钥不能为空")
  private String jwtSigningKey;

  /**
   * 无需验证即可访问的请求路径，多个路径以英文逗号分隔，支持 AntPathMatcher 的匹配模式。
   */
  private String permitAllAntPatterns;
}
