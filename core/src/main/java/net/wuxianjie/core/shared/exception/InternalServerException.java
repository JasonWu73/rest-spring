package net.wuxianjie.core.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因程序内部问题才导致服务不可用的异常，对应 500 HTTP 状态码
 */
public class InternalServerException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

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
     * @param cause   导致本异常产生的异常
     */
    public InternalServerException(
            final String message,
            final Throwable cause
    ) {
        super(message, cause);
    }
}
