package net.wuxianjie.web.loginlog;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志表实体类。
 *
 * @author 吴仙杰
 */
@Data
public class LoginLog {

  /**
   * 日志 id。
   */
  private Integer logId;

  /**
   * 登录时间，格式为 yyyy-MM-dd HH:mm:ss。
   */
  private LocalDateTime loginTime;

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 用户名。
   */
  private String username;

  /**
   * 请求 IP。
   */
  private String requestIp;
}
