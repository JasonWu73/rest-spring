package net.wuxianjie.web.operationlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.operationlog.OperationLogData;

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
     * 日志 ID。
     */
    private Integer logId;

    /**
     * 操作时间，格式为 yyyy-MM-dd HH:mm:ss。
     */
    private LocalDateTime operationTime;

    /**
     * 用户 ID，当为开放 API 时，则为 null。
     */
    private Integer userId;

    /**
     * 用户名，当为开放 API 时，则为 null。
     */
    private String username;

    /**
     * 请求 IP。
     */
    private String requestIp;

    /**
     * 请求 URI。
     */
    private String requestUri;

    /**
     * 目标方法的全限定名。
     */
    private String methodName;

    /**
     * 目标方法的描述，即操作描述。
     */
    private String methodMessage;

    /**
     * 目标方法入参的 JSON 字符串。
     */
    private String paramJson;

    /**
     * 目标方法返回值的 JSON 字符串。
     */
    private String returnJson;

    public OperationLog(final OperationLogData logData) {
        this.operationTime = logData.getOperationTime();
        this.userId = logData.getOperatorId();
        this.username = logData.getOperatorName();
        this.requestIp = logData.getRequestIp();
        this.requestUri = logData.getRequestUri();
        this.methodName = logData.getMethodName();
        this.methodMessage = logData.getMethodMessage();
        this.paramJson = logData.getParamJson();
        this.returnJson = logData.getReturnJson();
    }
}
