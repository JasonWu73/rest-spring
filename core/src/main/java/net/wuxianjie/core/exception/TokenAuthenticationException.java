package net.wuxianjie.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class TokenAuthenticationException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public TokenAuthenticationException(String message) {
        super(message);
    }

    public TokenAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
