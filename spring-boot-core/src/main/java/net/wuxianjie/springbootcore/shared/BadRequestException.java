package net.wuxianjie.springbootcore.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 可笼统表示所有因客户端原因引起的操作失败，使用 400 HTTP 状态码。
 */
public class BadRequestException extends AbstractBaseException {

  @Getter
  private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
