package net.wuxianjie.core.exception;

/**
 * 表示客户端请求有误才导出服务不可用的异常，对应400 HTTP状态码
 *
 * @author 吴仙杰
 */
public class BadRequestException extends RuntimeException {

  /**
   * 构造因客户端请求有误才导致服务不可用的异常
   *
   * @param message 说明客户端请求是何问题才导致服务不可用
   */
  public BadRequestException(final String message) {
    super(message);
  }

  /**
   * 构造因客户端请求有误才导致出现问题的异常
   *
   * @param message 说明客户端请求是何问题才导致服务不可用
   * @param cause 导致本异常产生的异常
   */
  public BadRequestException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
