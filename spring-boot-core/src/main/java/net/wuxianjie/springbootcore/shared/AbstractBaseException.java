package net.wuxianjie.springbootcore.shared;

import net.wuxianjie.springbootcore.rest.ExceptionControllerAdvice;
import org.springframework.http.HttpStatus;

/**
 * 包含获取 HTTP 状态码的抽象异常，默认仅记录 {@code message}，即不记录栈的完整信息。
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

    public abstract HttpStatus getHttpStatus();
}
