package net.wuxianjie.core.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 表示因数据已存在而导致操作失败，对应 409 HTTP 状态。
 * 常用于因唯一值约束条件而重复创建数据时抛出
 */
public class DataConflictException extends AbstractBaseException {

    @Getter
    private final HttpStatus httpStatus = HttpStatus.CONFLICT;

    /**
     * 构造因数据已存在而导致操作失败的异常
     *
     * @param message 说明因什么数据而产生了操作冲突
     */
    public DataConflictException(final String message) {
        super(message);
    }

    /**
     * 构造因数据已存在而导致操作失败的异常
     *
     * @param message 说明因什么数据而产生了操作冲突
     * @param cause   导致本异常产生的异常
     */
    public DataConflictException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
