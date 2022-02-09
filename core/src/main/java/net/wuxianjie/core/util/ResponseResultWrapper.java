package net.wuxianjie.core.util;

import net.wuxianjie.core.constant.ErrorCode;
import net.wuxianjie.core.domain.RestResponse;

/**
 * REST API统一结果封装类
 *
 * @author 吴仙杰
 */
public final class ResponseResultWrapper {

  /**
   * 构造请求成功时的响应对象
   *
   * @param data 具数据结果
   * @param <T> 数据结果的具体类型
   * @return 服务器响应的结果对象
   */
  public static <T> RestResponse<T> success(final T data) {
    final RestResponse<T> result = new RestResponse<>();
    result.setError(ErrorCode.SUCCESS);
    result.setData(data);
    return result;
  }

  /**
   * 构造请求失败时的响应对象
   *
   * @param failMsg 说明请求为何失败的提示信息
   * @return 服务器响应的结果对象
   */
  public static RestResponse<Void> fail(final String failMsg) {
    final RestResponse<Void> result = new RestResponse<>();
    result.setError(ErrorCode.FAIL);
    result.setMessage(failMsg);
    return result;
  }
}
