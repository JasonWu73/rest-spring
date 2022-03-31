package net.wuxianjie.springbootcore.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示当前 API 与外部系统有交到，且因为外部系统无法响应预期结果而导致本次请求处理失败的异常，使用 503 HTTP 状态码，并以 ERROR 级别记录日志，但不记录异常堆栈信息。
 *
 * @author 吴仙杰
 */
public class ExternalException extends AbstractServerBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.SERVICE_UNAVAILABLE;

    public ExternalException(final String message) {
        super(message);
    }

    public ExternalException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
