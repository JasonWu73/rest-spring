package net.wuxianjie.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
