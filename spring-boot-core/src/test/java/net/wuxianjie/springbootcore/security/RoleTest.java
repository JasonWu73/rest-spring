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
    String value = Role.ADMIN.value();

    // when
    Optional<Role> actual = Role.resolve(value);

    // then
    assertThat(actual.orElseThrow()).isEqualTo(Role.ADMIN);
  }

  @Test
  @DisplayName("不可解析为枚举值")
  void canNotResolve() {
    // given
    String value = "super";

    // when
    Optional<Role> actual = Role.resolve(value);

    // then
    assertThat(actual.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("不可解析为枚举值：解析 null 值")
  void canNotResolveNull() {
    // given
    // when
    Optional<Role> actual = Role.resolve(null);

    // then
    assertThat(actual.isEmpty()).isTrue();
  }
}