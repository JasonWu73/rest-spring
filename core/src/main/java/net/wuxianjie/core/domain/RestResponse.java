package net.wuxianjie.core.domain;

import lombok.Data;
import net.wuxianjie.core.constant.ErrorCode;

/**
 * REST API统一响应数据结果
 *
 * @param <T> 主要数据结果的类类型
 * @author 吴仙杰
 */
@Data
public class RestResponse<T> {

  /** 本次请求的error code */
  private ErrorCode error;

  /** 失败时提示信息，当仅且当返回错误结果时才存在 */
  private String message;

  /** 主要数据结果 */
  private T data;
}