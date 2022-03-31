package net.wuxianjie.springbootcore.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * REST API 响应结果的包装器。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResultWrapper {

    /**
     * 构造服务响应成功的结果。
     *
     * @param data 具体数据
     * @param <T>  具体数据的类型
     * @return 服务响应成功的结果
     */
    public static <T> ApiResult<T> success(final T data) {
        final ApiResult<T> result = new ApiResult<>();
        result.setError(ApiErrorCode.SUCCESS);
        result.setData(data);
        return result;
    }

    /**
     * 构造服务响应失败的结果。
     *
     * @param errorMessage 失败的原因
     * @return 服务响应失败的结果
     */
    public static ApiResult<Void> fail(final String errorMessage) {
        final ApiResult<Void> result = new ApiResult<>();
        result.setError(ApiErrorCode.FAIL);
        result.setErrMsg(errorMessage);
        return result;
    }
}
