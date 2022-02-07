package net.wuxianjie.core.model.dto;

import lombok.Data;
import net.wuxianjie.core.util.ResponseStatus;

/**
 * REST API 统一响应结果数据传输对象
 *
 * @param <T> 主要数据结果的类类型
 * @author 吴仙杰
 */
@Data
public class RestDto<T> {

  /**
   * 本次请求的服务器响应状态。
   */
  private ResponseStatus status;

  /**
   * 失败时提示信息，当仅且当返回错误结果时才存在。
   */
  private String message;

  /**
   * 主要数据结果。
   */
  private T data;
}
