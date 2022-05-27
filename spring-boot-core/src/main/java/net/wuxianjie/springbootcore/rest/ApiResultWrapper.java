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
  public static <T> ApiResult<T> success(T data) {
    ApiResult<T> result = new ApiResult<>();
    result.setErrorCode(ApiErrorCode.SUCCESS);
    result.setData(data);
    return result;
  }

  /**
   * 构造服务响应失败的结果。
   *
   * @param errorMessage 错误信息
   * @return 服务响应失败的结果
   */
  public static ApiResult<Void> fail(String errorMessage) {
    ApiResult<Void> result = new ApiResult<>();
    result.setErrorCode(ApiErrorCode.FAIL);
    result.setErrorMessage(errorMessage);
    return result;
  }
}
