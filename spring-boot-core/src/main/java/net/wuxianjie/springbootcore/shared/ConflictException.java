package net.wuxianjie.springbootcore.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因客户端提交的数据和服务中已有数据存在冲突而导致的操作失败，使用 409 HTTP 状态码。
 *
 * @author 吴仙杰
 */
public class ConflictException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
