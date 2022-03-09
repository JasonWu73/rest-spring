package net.wuxianjie.core.shared;

import net.wuxianjie.core.rest.ControllerErrorAdvice;
import org.springframework.http.HttpStatus;

/**
 * 包含获取 HTTP 状态码的抽象异常。
 *
 * @see ControllerErrorAdvice
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
