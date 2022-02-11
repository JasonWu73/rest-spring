package net.wuxianjie.core.exception;

/**
 * 表示Token身份认证失败的异常，对应401 HTTP状态码
 *
 * @author 吴仙杰
 */
public class TokenAuthenticationException extends RuntimeException {

  /**
   * 构造Token身份认证失败时的异常
   *
   * @param message 说明是何原因导致Token身份认证失败
   */
  public TokenAuthenticationException(final String message) {
    super(message);
  }

   /**
   * 构造Token身份认证失败时的异常
   *
   * @param message 说明是何原因导致Token身份认证失败
    * @param cause 导致本异常产生的异常
   */
  public TokenAuthenticationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
