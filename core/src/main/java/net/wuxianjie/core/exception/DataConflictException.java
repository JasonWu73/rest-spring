package net.wuxianjie.core.exception;

/**
 * 表示因数据已存在而导致操作冲突的异常，对应409 HTTP状态
 *
 * @author 吴仙杰
 */
public class DataConflictException extends RuntimeException {

  /**
   * 构造因数据已存在而导致操作冲突的异常
   *
   * @param message 说明因什么数据而产生了操作冲突
   */
  public DataConflictException(final String message) {
    super(message);
  }

  /**
   * 构造因数据已存在而导致操作冲突的异常
   *
   * @param message 说明因什么数据而产生了操作冲突
   * @param cause 导致本异常产生的异常
   */
  public DataConflictException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
