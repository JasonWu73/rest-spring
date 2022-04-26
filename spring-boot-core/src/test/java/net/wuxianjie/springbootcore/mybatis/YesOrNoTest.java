package net.wuxianjie.springbootcore.mybatis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
class YesOrNoTest {

  @Test
  @DisplayName("可识别为 YesOrNo 枚举")
  void canResolve() {
    // given
    final int value = YesOrNo.YES.value();

    // when
    final Optional<YesOrNo> actual = YesOrNo.resolve(value);

    // then
    assertThat(actual.orElseThrow()).isEqualTo(YesOrNo.YES);
  }

  @Test
  @DisplayName("不可识为 YesOrNo 枚举")
  void canNotResolve() {
    // given
    final int value = -1;

    // when
    final Optional<YesOrNo> actual = YesOrNo.resolve(value);

    // then
    assertThat(actual.isEmpty()).isTrue();
  }
}