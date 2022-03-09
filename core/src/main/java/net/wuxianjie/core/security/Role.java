package net.wuxianjie.core.security;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.lang.Nullable;

/**
 * 内置可用的 Spring Security 角色。
 */
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

    @Nullable
    public static Role resolve(String value) {
        for (Role role : VALUES) {
            if (role.value.equals(value)) {
                return role;
            }
        }

        return null;
    }
}
