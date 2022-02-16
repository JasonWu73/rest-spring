package net.wuxianjie.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 账号信息
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  /** 账号ID，即用户ID */
  private Integer accountId;

  /** 账号名称，即用户名 */
  private String accountName;

  /** 账号密码 */
  private String hashedPassword;

  /** 账号角色，以{@code ,}分隔，全部为小写字母，且不包含{@code ROLE_}前缀 */
  private String roles;
}
