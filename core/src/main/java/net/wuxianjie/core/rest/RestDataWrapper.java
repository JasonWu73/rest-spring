package net.wuxianjie.core.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * REST API 服务响应结果封装器。
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestDataWrapper {

  @NonNull
  public static <T> RestData<T> success(T data) {
    final RestData<T> result = new RestData<>();

    result.setError(ErrorCode.SUCCESS);
    result.setData(data);

    return result;
  }

  @NonNull
  public static RestData<Void> fail(String failMsg) {
    final RestData<Void> result = new RestData<>();

    result.setError(ErrorCode.FAIL);
    result.setErrMsg(failMsg);

    return result;
  }
}
