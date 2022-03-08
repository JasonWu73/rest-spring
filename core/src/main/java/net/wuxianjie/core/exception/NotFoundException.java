package net.wuxianjie.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
