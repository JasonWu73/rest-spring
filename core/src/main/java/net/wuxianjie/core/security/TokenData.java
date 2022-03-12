package net.wuxianjie.core.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenData {

  /**
   * Token 的有效期，单位秒。
   */
  private Integer expiresIn;

  /**
   * 用于 API 鉴权的 Token，请求头中携带：`Authorization: Bearer accessToken`。
   */
  private String accessToken;

  /**
   * 用于刷新鉴权信息的 Token。
   */
  private String refreshToken;
}
