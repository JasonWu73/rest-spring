package net.wuxianjie.springbootcore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因为外部系统（外部 API）无法响应预期结果而导致本次请求处理失败的异常，使用 503 HTTP 状态码，并以 ERROR 级别记录日志，但不记录异常堆栈信息。
 *
 * @author 吴仙杰
 */
public class ExternalException extends AbstractServerBaseException {

  @Getter
  private final HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

  public ExternalException(String message) {
    super(message);
  }

  public ExternalException(String message, Throwable cause) {
    super(message, cause);
  }
}
