package net.wuxianjie.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

  /** 用户ID */
  private Integer id;

  /** 最近用户信息的修改时间 */
  private LocalDateTime modifyTime;

  /** 用户名 */
  private String name;

  /** 账号角色，以{@code ,}分隔，全部为小写字母，且不包含{@code ROLE_}前缀 */
  private String roles;
}
