package net.wuxianjie.springbootcore.rest;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * REST API 服务响应结果的错误码。
 *
 * @author 吴仙杰
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public enum ErrorCode {

    /**
     * 请求成功。
     */
    SUCCESS(0),

    /**
     * 请求失败。
     */
    FAIL(1);

    @JsonValue
    private final int value;
}
