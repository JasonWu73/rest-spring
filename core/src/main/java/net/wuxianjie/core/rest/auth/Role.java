package net.wuxianjie.core.rest.auth;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 权限管理所支持的角色
 */
@Getter
@ToString
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum Role {

    /**
     * 普通用户
     */
    USER("user"),

    /**
     * 管理员
     */
    ADMIN("admin");

    private static final Role[] VALUES;

    static {
        VALUES = values();
    }

    @JsonValue
    private final String value;

    /**
     * 从指定值解析为枚举值
     *
     * @param role 角色代码，区分大小写
     * @return 若没有找到则为 {@code null}
     */
    public static Role resolve(final String role) {
        // 使用缓存的 `VALUES` 而不是 `values()` 来防止数组分配
        for (final Role authRole : VALUES) {
            if (authRole.value.equals(role)) {
                return authRole;
            }
        }
        return null;
    }
}
