package net.wuxianjie.springbootcore.operationlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志数据。
 *
 * @author 吴仙杰
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationLogData {

    /**
     * 操作员 id，当为开放 API 时，则为 {@code null}。
     */
    private Integer operatorId;

    /**
     * 操作员账号，当为开放 API 时，则为 {@code null}。
     */
    private String operatorName;

    /**
     * 操作时间。
     */
    private LocalDateTime operationTime;

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
}
