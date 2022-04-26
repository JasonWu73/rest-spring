package net.wuxianjie.springbootcore.security;

import lombok.Data;

/**
 * @author 吴仙杰
 */
@Data
class UserDetails implements TokenUserDetails {

  private Integer accountId;
  private String accountName;
  private String roles;
  private String accessToken;
  private String refreshToken;
}
