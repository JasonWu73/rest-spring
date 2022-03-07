package net.wuxianjie.core.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 客户端请求的资源不存在，对应 404 HTTP 状态码
 */
public class NotFoundException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    /**
     * 构造因客户端请求资源不存在才导致服务不可用的异常
     *
     * @param message 说明客户端请求的什么资源不存在
     */
    public NotFoundException(final String message) {
        super(message);
    }

    /**
     * 构造因客户端请求资源不存在才导致服务不可用的异常
     *
     * @param message 说明客户端请求的什么资源不存在
     * @param cause   导致本异常产生的异常
     */
    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
