package net.wuxianjie.springbootcore.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因 Token 认证不通过而导致的操作失败，使用 401 HTTP 状态码。
 *
 * @author 吴仙杰
 */
public class TokenAuthenticationException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public TokenAuthenticationException(final String message) {
        super(message);
    }

    public TokenAuthenticationException(final String message,
                                        final Throwable cause) {
        super(message, cause);
    }
}
