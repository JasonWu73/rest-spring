package net.wuxianjie.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

  /** 日志ID */
  private Integer operationLogId;

  /** 日志的操作时间 */
  private LocalDateTime operationTime;

  /** 用户ID，即操作人ID */
  private Integer userId;

  /** 用户名，即操作人名称 */
  private String username;

  /** 操作的详细内容 */
  private String message;
}
