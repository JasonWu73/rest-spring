package net.wuxianjie.springbootcore.shared.exception;

import net.wuxianjie.springbootcore.rest.ExceptionControllerAdvice;

/**
 * 表示非客户端请求原因而引起的异常，使用 5xx HTTP 状态码，并以 ERROR 级别记录日志，但不记录异常堆栈信息。
 *
 * @author 吴仙杰
 * @see ExceptionControllerAdvice
 */
public abstract class AbstractServerBaseException extends AbstractBaseException {

    public AbstractServerBaseException(final String message) {
        super(message);
    }

    public AbstractServerBaseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
