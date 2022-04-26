package net.wuxianjie.springbootcore.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 响应结果。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenData {

  /**
   * Token 的有效期，单位秒。
   */
  private Integer expiresIn;

  /**
   * 用于 API 鉴权的 Token，在请求头中携带：{@code Authorization: Bearer accessToken}。
   */
  private String accessToken;

  /**
   * 用于刷新鉴权信息的 Token。
   */
  private String refreshToken;
}
