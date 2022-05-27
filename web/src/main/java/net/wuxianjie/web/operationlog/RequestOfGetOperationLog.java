package net.wuxianjie.web.operationlog;

import lombok.Data;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 查询操作日志请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class RequestOfGetOperationLog {

  /**
   * 开始日期，包含，格式为 yyyy-MM-dd。
   */
  @Pattern(message = "开始日期格式错误", regexp = "(^$|^\\d{4}-\\d{2}-\\d{2}$)")
  private String startDate;
  private LocalDateTime startTimeInclusive;

  /**
   * 结束日期，包含，格式为 yyyy-MM-dd。
   */
  @Pattern(message = "结束日期格式错误", regexp = "(^$|^\\d{4}-\\d{2}-\\d{2}$)")
  private String endDate;
  private LocalDateTime endTimeInclusive;

  /**
   * 用户名。
   */
  private String username;

  /**
   * 请求 IP。
   */
  private String requestIp;

  /**
   * 操作描述。
   */
  private String methodMessage;
}
