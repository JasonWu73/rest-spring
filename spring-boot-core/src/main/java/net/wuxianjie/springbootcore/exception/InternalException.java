package net.wuxianjie.springbootcore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示非客户端请求原因而引起的异常，使用 500 HTTP 状态码，并以 ERROR 级别记录日志，但不记录异常堆栈信息。
 *
 * @author 吴仙杰
 */
public class InternalException extends AbstractServerBaseException {

  @Getter
  private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

  public InternalException(String message) {
    super(message);
  }

  public InternalException(String message, Throwable cause) {
    super(message, cause);
  }
}
