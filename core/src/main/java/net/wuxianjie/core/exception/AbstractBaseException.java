package net.wuxianjie.core.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractBaseException extends RuntimeException {

    public AbstractBaseException(final String message) {
        super(message);
    }

    public AbstractBaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public abstract HttpStatus getHttpStatus();
}
