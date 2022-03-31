package net.wuxianjie.springbootcore.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
class RoleTest {

    @Test
    @DisplayName("可解析为枚举值")
    void canResolve() {
        // given
        final String value = "admin";

        // when
        final Optional<Role> actual = Role.resolve(value);

        // then
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("不可解析为枚举值")
    void canNotResolve() {
        // given
        final String value = "super";

        // when
        final Optional<Role> actual = Role.resolve(value);

        // then
        assertThat(actual.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("将 null 解析为枚举值")
    void canNotResolveNull() {
        // given
        final String value = null;

        // when
        final Optional<Role> actual = Role.resolve(value);

        // then
        assertThat(actual.isEmpty()).isTrue();
    }
}