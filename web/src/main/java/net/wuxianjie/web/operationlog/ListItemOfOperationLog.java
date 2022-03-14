package net.wuxianjie.web.operationlog;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListItemOfOperationLog {

  /**
   * 操作日志 ID。
   */
  private Integer operationLogId;

  /**
   * 操作时间。
   */
  private LocalDateTime operationTime;

  /**
   * 用户 ID，即操作人 ID。
   */
  private Integer userId;

  /**
   * 用户名，即操作人名称。
   */
  private String username;

  /**
   * 具体的操作消息。
   */
  private String message;

  public ListItemOfOperationLog(OperationLog operationLog) {
    BeanUtil.copyProperties(operationLog, this);
  }
}
