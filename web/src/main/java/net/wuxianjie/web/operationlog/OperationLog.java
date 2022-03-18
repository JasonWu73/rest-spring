package net.wuxianjie.web.operationlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志表。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

    /**
     * 操作日志 ID。
     */
    private Integer operationLogId;

    /**
     * 操作时间。
     */
    private LocalDateTime operationTime;

    /**
     * 操作人 ID，即用户 ID。
     */
    private Integer userId;

    /**
     * 操作人名称，即用户名。
     */
    private String username;

    /**
     * 详细操作内容。例如：
     *
     * <ul>
     *     <li>新增 xxx 数据【ID：xxx，标识名：xxx】</li>
     *     <li>删除 xxx 数据【ID：xxx，标识名：xxx】</li>
     *     <li>修改 xxx 数据【ID：xxx，标识名：xxx】：将【xxx】修改为【xxx】</li>
     * </ul>
     */
    private String message;
}
