package net.wuxianjie.web.operationlog;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class OperationLogDto {

    private Integer operationLogId;

    private LocalDateTime operationTime;

    private Integer userId;

    private String username;

    private String message;

    public OperationLogDto(final OperationLog operationLog) {
        this.operationLogId = operationLog.getOperationLogId();
        this.operationTime = operationLog.getOperationTime();
        this.userId = operationLog.getUserId();
        this.username = operationLog.getUsername();
        this.message = operationLog.getMessage();
    }
}
