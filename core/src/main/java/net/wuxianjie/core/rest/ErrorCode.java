package net.wuxianjie.core.rest;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum ErrorCode {

    SUCCESS(0),

    FAIL(1);

    @JsonValue
    private final int value;
}
