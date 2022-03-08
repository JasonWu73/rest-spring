package net.wuxianjie.core.security;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum Role {

    USER("user"),

    ADMIN("admin");

    private static final Role[] VALUES;

    static {
        VALUES = values();
    }

    @JsonValue
    private final String value;

    /**
     * 将常量值解析为枚举值，无法解析则返回 null
     */
    public static Role resolve(String value) {
        for (Role roleEnum : VALUES) {
            if (roleEnum.value.equals(value)) {
                return roleEnum;
            }
        }

        return null;
    }
}
