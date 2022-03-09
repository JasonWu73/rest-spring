package net.wuxianjie.core.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因客户端提交已存在数据而引起的操作失败，使用 409 HTTP 状态码。
 */
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
