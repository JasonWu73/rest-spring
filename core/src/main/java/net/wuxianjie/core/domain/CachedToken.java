package net.wuxianjie.core.domain;

import lombok.Data;

/**
 * 程序内部的Token数据传输对象，用于缓存及Spring Security认证后的Principal
 *
 * @author 吴仙杰
 */
@Data
public class CachedToken {

  /** 用于访问接口的Access Token */
  private String accessToken;

  /** 只用于刷新的Refresh Token */
  private String refreshToken;

  /** 该Token对应的账号ID */
  private Integer accountId;

  /** 该Token对应的账号名称 */
  private String accountName;

  /** 账号角色，以{@code ,}分隔，全部为小写字母，且不包含{@code ROLE_}前缀 */
  private String roles;
}
