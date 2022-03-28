package net.wuxianjie.springbootcore.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    @DisplayName("可解析为枚举值")
    void canResolve() {
        // given
        String value = "admin";

        // when
        Optional<Role> actual = Role.resolve(value);

        // then
        assertThat(actual).isEqualTo(Optional.of(Role.ADMIN));
    }

    @Test
    @DisplayName("不可解析为枚举值")
    void canNotResolve() {
        // given
        String value = "super";

        // when
        Optional<Role> actual = Role.resolve(value);

        // then
        assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("将 null 解析为枚举值")
    void canNotResolveNull() {
        // given
        String value = null;

        // when
        Optional<Role> actual = Role.resolve(value);

        // then
        assertThat(actual).isEqualTo(Optional.empty());
    }
}