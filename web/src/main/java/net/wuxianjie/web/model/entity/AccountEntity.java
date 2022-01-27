package net.wuxianjie.web.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库账号表 {@code account} 的实体映射类
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

  /** 账号 ID */
  private Integer id;

  /** 用户名 */
  private String name;

  /** 密码 */
  private String password;

  /** 角色字符串, 以 {@code ,} 分隔, 全部为小写字母, 且不包含 {@code ROLE_} 前缀 */
  private String roles;
}
