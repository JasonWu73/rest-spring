package net.wuxianjie.web.operationlog;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志列表项数据传输对象。
 *
 * @author 吴仙杰
 */
@Data
@JsonInclude
public class LogItemDto {

  /**
   * 日志 id。
   */
  private Integer logId;

  /**
   * 操作时间，格式为 yyyy-MM-dd HH:mm:ss。
   */
  private LocalDateTime operationTime;

  /**
   * 用户 id，当为开放 API 时，则为 null。
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
}
