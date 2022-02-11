package net.wuxianjie.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回给前端的Token数据
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {

  /** Access Token的有效期（以秒为单位） */
  private int expiresIn;

  /** 用于访问接口的Access Token */
  private String accessToken;

  /** 只用于刷新的Refresh Token */
  private String refreshToken;
}
