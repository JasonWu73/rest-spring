package net.wuxianjie.core.domain;

import lombok.Data;

/**
 * 程序内部的 Token 数据传输对象, 用于缓存及 Spring Security 认证后的 Principal
 *
 * @author 吴仙杰
 */
@Data
public class CachedToken {

  /** 用于访问接口的 Access Token */
  private String accessToken;

  /** 只用于刷新的 Refresh Token */
  private String refreshToken;

  /** 该 Token 对应的账号 ID */
  private Integer accountId;

  /** 该 Token 对应的账号名称 */
  private String accountName;

  /** 角色字符串, 以 {@code ,} 分隔, 全部为小写字母, 且不包含 {@code ROLE_} 前缀 */
  private String roles;
}
