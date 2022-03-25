package net.wuxianjie.springbootcore.shared;

import net.wuxianjie.springbootcore.rest.ExceptionControllerAdvice;
import org.springframework.http.HttpStatus;

/**
 * 自定义异常的基类：包含获取 HTTP 状态码的抽象异常，且不记录异常堆栈信息。
 * <p>
 * 默认子类异常仅以 WARN 级别记录日志，针对服务器异常则可使用子类 {@link InternalServerException}，它以 ERROR 级别记录日志。
 * </p>
 *
 * @author 吴仙杰
 * @see ExceptionControllerAdvice
 */
public abstract class AbstractBaseException extends RuntimeException {

    public AbstractBaseException(String message) {
        super(message);
    }

    public AbstractBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 获取以响应何种 HTTP 状态码来指明异常信息。
     *
     * @return HTTP 状态码
     */
    public abstract HttpStatus getHttpStatus();
}
