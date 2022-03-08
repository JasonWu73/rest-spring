package net.wuxianjie.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class DataConflictException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public DataConflictException(String message) {
        super(message);
    }

    public DataConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
