package net.wuxianjie.springbootcore.mybatis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
class YesOrNoTest {

    @DisplayName("可识别数值为 YesOrNo 枚举")
    @Test
    void itShouldCheckWhenCanResolve() {
        // given
        int value = 1;

        // when
        Optional<YesOrNo> actual = YesOrNo.resolve(value);

        // then
        assertThat(actual).isEqualTo(Optional.of(YesOrNo.YES));
    }

    @DisplayName("不可识别数值为 YesOrNo 枚举")
    @Test
    void itShouldCheckWhenCanNotResolve() {
        // given
        int value = 2;

        // when
        Optional<YesOrNo> actual = YesOrNo.resolve(value);

        // then
        assertThat(actual).isEqualTo(Optional.empty());
    }
}