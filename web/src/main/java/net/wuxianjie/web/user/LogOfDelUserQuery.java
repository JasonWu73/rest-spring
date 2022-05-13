package net.wuxianjie.web.user;

import lombok.Data;

/**
 * 删除用户参数，需填充相应字段以便记录操作日志。
 *
 * @author 吴仙杰
 */
@Data
public class LogOfDelUserQuery {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 用户名。
   */
  private String username;
}
