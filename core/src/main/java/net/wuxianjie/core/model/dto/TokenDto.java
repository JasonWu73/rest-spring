package net.wuxianjie.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回给前端的 Token 数据传输对象
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {

  /** Access Token 的有效期 (秒为单位) */
  private int expiresIn;

  /** 用于访问接口的 Access Token */
  private String accessToken;

  /** 只用于刷新的 Refresh Token */
  private String refreshToken;
}
