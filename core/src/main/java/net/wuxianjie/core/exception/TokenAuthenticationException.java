package net.wuxianjie.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示 Token 身份认证失败的异常，对应 401 HTTP 状态码
 */
public class TokenAuthenticationException extends RuntimeException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    /**
     * 构造 Token 身份认证失败时的异常
     *
     * @param message 说明是何原因导致 Token 身份认证失败
     */
    public TokenAuthenticationException(final String message) {
        super(message);
    }

    /**
     * 构造 Token 身份认证失败时的异常
     *
     * @param message 说明是何原因导致 Token 身份认证失败
     * @param cause   导致本异常产生的异常
     */
    public TokenAuthenticationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
