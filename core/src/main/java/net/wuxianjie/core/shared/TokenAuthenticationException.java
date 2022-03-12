package net.wuxianjie.core.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因 Token 认证失败而引起的操作失败，使用 401 HTTP 状态码。
 */
public class TokenAuthenticationException extends AbstractBaseException {

  @Getter
  private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

  public TokenAuthenticationException(String message) {
    super(message);
  }

  public TokenAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
