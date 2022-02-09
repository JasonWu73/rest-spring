package net.wuxianjie.core.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * REST API返回结果的响应状态
 *
 * @author 吴仙杰
 */
@Accessors(fluent = true)
@Getter
@ToString
@RequiredArgsConstructor
public enum ErrorCode {

  /** 服务器本次请求响应成功 */
  SUCCESS(0),

  /** 服务器对本次请求响应失败 */
  FAIL(1);

  @JsonValue
  private final int value;
}
