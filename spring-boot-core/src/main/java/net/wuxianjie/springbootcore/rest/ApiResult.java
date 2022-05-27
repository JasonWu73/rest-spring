package net.wuxianjie.springbootcore.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST API 响应结果。
 *
 * @param <T> 具体数据的类型
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

  /**
   * 错误码：0：成功，1：失败。
   */
  private ApiErrorCode errorCode;

  /**
   * 错误信息。
   */
  private String errorMessage;

  /**
   * 具体数据。
   */
  private T data;
}
