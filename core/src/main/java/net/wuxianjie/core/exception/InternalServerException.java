package net.wuxianjie.core.exception;

/**
 * 表示程序内部有问题才导致服务不可用的异常，对应500 HTTP状态码
 *
 * @author 吴仙杰
 */
public class InternalServerException extends RuntimeException {

  /**
   * 构造因程序本身问题才导致服务不可用的异常
   *
   * @param message 说明程序是何问题才导致服务不可用
   */
  public InternalServerException(final String message) {
    super(message);
  }

  /**
   * 构造因程序本身问题才导致服务不可用的异常
   *
   * @param message 说明程序是何问题才导致服务不可用
   * @param cause 导致本异常产生的异常
   */
  public InternalServerException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
