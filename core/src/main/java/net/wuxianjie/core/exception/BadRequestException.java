package net.wuxianjie.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因客户端请求有误才导致服务不可用，对应 400 HTTP 状态码。
 * 常用于参数校验失败时抛出异常
 */
public class BadRequestException extends RuntimeException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

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
     * @param cause   导致本异常产生的异常
     */
    public BadRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
