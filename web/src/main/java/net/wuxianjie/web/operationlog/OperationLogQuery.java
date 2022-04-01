package net.wuxianjie.web.operationlog;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 操作日志查询参数。
 *
 * @author 吴仙杰
 */
@Data
public class OperationLogQuery {

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
     * 用户名，当为开放 API 时，则不存在。
     */
    @Length(message = "用户名最长不能超过 100 个字符", max = 100)
    private String username;

    /**
     * 请求 IP。
     */
    @Length(message = "请求 IP 最长不能超过 100 个字符", max = 100)
    private String requestIp;

    /**
     * 操作描述。
     */
    @Length(message = "操作描述最长不能超过 100 个字符", max = 100)
    private String methodMessage;
}
