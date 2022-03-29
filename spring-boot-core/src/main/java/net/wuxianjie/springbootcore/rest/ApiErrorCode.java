package net.wuxianjie.springbootcore.rest;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * REST API 响应结果的错误码。
 *
 * @author 吴仙杰
 */
@Getter
@ToString
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum ApiErrorCode {

    /**
     * 服务响应成功。
     */
    SUCCESS(0),

    /**
     * 服务响应失败。
     */
    FAIL(1);

    @JsonValue
    private final int value;
}
