package net.wuxianjie.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InternalServerException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
