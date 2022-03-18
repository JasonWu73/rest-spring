package net.wuxianjie.springbootcore.security;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.Optional;

/**
 * 内置可用的 Spring Security 角色。
 *
 * @author 吴仙杰
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public enum Role {

    USER("user"),

    ADMIN("admin");

    private static final Role[] VALUES;

    static {
        VALUES = values();
    }

    @JsonValue
    private final String value;

    public static Optional<Role> resolve(String value) {
        for (Role role : VALUES) {
            if (Objects.equals(value, role.value)) {
                return Optional.of(role);
            }
        }

        return Optional.empty();
    }
}
