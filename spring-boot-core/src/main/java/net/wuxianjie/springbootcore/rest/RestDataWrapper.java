package net.wuxianjie.springbootcore.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * REST API 服务响应结果封装器。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestDataWrapper {

    public static <T> RestData<T> success(T data) {
        RestData<T> result = new RestData<>();
        result.setError(ErrorCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static RestData<Void> fail(String failMsg) {
        RestData<Void> result = new RestData<>();
        result.setError(ErrorCode.FAIL);
        result.setErrMsg(failMsg);
        return result;
    }
}
