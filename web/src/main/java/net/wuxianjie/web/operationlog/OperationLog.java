package net.wuxianjie.web.operationlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

    /**
     * 操作日志 ID
     */
    private Integer operationLogId;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 用户 ID，即操作人 ID
     */
    private Integer userId;

    /**
     * 用户名，即操作人名称
     */
    private String username;

    /**
     * 操作的详细内容
     */
    private String message;
}
