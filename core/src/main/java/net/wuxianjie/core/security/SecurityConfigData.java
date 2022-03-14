package net.wuxianjie.core.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 与 Token 认证等安全管理相关的配置数据。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityConfigData {

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
