package net.wuxianjie.web.user;

import lombok.Data;

/**
 * 删除用户查询参数。
 *
 * @author 吴仙杰
 */
@Data
public class DelUserQuery {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 用户名。
   */
  private String username;
}
