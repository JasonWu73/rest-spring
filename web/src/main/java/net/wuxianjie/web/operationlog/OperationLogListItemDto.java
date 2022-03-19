package net.wuxianjie.web.operationlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志分页列表项。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogListItemDto {

    /**
     * 操作日志 ID。
     */
    private Integer operationLogId;

    /**
     * 操作时间，格式为 yyyy-MM-dd HH:mm:ss。
     */
    private LocalDateTime operationTime;

    /**
     * 操作人 ID。
     */
    private Integer userId;

    /**
     * 操作人名称。
     */
    private String username;

    /**
     * 详细操作内容。
     */
    private String message;
}
