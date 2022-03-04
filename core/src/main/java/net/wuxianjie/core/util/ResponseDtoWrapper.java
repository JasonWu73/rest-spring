package net.wuxianjie.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wuxianjie.core.constant.ErrorCode;
import net.wuxianjie.core.dto.ResponseDto;

/**
 * REST API 统一结果封装器
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseDtoWrapper {

    /**
     * 构造请求成功时的响应对象
     *
     * @param data 具体结果
     * @param <T>  具体结果的类型
     * @return ResponseDto
     */
    public static <T> ResponseDto<T> success(final T data) {
        final ResponseDto<T> result = new ResponseDto<>();
        result.setError(ErrorCode.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 构造请求失败时的响应对象
     *
     * @param failMsg 说明请求为何失败的提示信息
     * @return ResponseDto
     */
    public static ResponseDto<Void> fail(final String failMsg) {
        final ResponseDto<Void> result = new ResponseDto<>();
        result.setError(ErrorCode.FAIL);
        result.setErrMsg(failMsg);
        return result;
    }
}
