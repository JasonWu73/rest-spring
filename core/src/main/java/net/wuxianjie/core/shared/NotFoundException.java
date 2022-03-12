package net.wuxianjie.core.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因客户端请求不存在数据而引起的操作失败，使用 404 HTTP 状态码。
 */
public class NotFoundException extends AbstractBaseException {

  @Getter
  private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
