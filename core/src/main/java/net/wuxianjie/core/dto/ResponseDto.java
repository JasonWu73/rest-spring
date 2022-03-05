package net.wuxianjie.core.dto;

import lombok.Data;
import net.wuxianjie.core.constant.ErrorCode;

/**
 * REST API 统一响应结果
 *
 * @param <T> 具体结果的类类型
 */
@Data
public class ResponseDto<T> {

    /**
     * 错误码，0 代表成功，非 0 代表失败
     */
    private ErrorCode error;

    /**
     * 失败时提示消息
     */
    private String errMsg;

    /**
     * 具体结果
     */
    private T data;
}
