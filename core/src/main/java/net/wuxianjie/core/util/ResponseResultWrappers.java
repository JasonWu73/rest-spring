package net.wuxianjie.core.util;

import net.wuxianjie.core.model.ResponseResult;

/**
 * REST API 统一结果封装类。
 *
 * @author 吴仙杰
 */
public final class ResponseResultWrappers {

  /**
   * 构造请求成功时的响应对象。
   *
   * @param data 具数据结果
   * @param <T> 数据结果的具体类型
   * @return 服务器响应的结果对象
   */
  public static <T> ResponseResult<T> success(final T data) {
    final ResponseResult<T> result = new ResponseResult<>();

    result.setStatus(ResponseStatus.SUCCESS);
    result.setData(data);

    return result;
  }

  /**
   * 构造请求失败时的响应对象。
   *
   * @param failMsg 说明请求为何失败的提示信息
   * @return 服务器响应的结果对象
   */
  public static ResponseResult<Void> fail(final String failMsg) {
    final ResponseResult<Void> result = new ResponseResult<>();

    result.setStatus(ResponseStatus.FAIL);
    result.setMessage(failMsg);

    return result;
  }
}
