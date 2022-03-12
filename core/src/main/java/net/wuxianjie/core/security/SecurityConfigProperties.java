package net.wuxianjie.core.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 与 Token 认证等安全管理相关的配置属性说明。
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "core.security")
public class SecurityConfigProperties {

  /**
   * JWT 签名密钥。
   */
  private String jwtSigningKey;

  /**
   * 无需鉴权即可访问的请求路径，
   * 多个路径以英文逗号分隔，支持 AntPathMatcher 的匹配模式。
   */
  private String permitAllAntPatterns;
}
