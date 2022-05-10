package net.wuxianjie.web.user;

import lombok.Data;

/**
 * 删除用户请求参数。
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
