package net.wuxianjie.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.web.model.OperationLog;

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
