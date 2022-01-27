package net.wuxianjie.core.util;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * REST API 返回结果的响应状态。
 *
 * @author 吴仙杰
 */
@ToString
@RequiredArgsConstructor
public enum ResponseStatus {

  /**
   * 服务器本次请求响应成功。
   */
  SUCCESS("success"),

  /**
   * 服务器对本次请求响应失败。
   */
  FAIL("fail");

  @JsonValue
  public final String value;
}
