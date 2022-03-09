package net.wuxianjie.core.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因服务器本身原因而引起的操作失败，使用 409 HTTP 状态码。
 */
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
