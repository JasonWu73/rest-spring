package net.wuxianjie.core.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
