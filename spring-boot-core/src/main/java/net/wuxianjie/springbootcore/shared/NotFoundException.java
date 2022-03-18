package net.wuxianjie.springbootcore.shared;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因客户端要请求不存在的数据而引起的操作失败，使用 404 HTTP 状态码。
 *
 * @author 吴仙杰
 */
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
