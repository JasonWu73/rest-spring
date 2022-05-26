package net.wuxianjie.springbootcore.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因 Token 身份验证不通过而导致的操作失败，使用 401 HTTP 状态码。
 *
 * @author 吴仙杰
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
