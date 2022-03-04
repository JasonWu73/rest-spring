package net.wuxianjie.core.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * REST API 响应结果的错误码
 */
@Getter
@ToString
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum ErrorCode {

    /**
     * 成功
     */
    SUCCESS(0),

    /**
     * 失败
     */
    FAIL(1);

    @JsonValue
    private final int value;
}
