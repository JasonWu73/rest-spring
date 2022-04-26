package net.wuxianjie.springbootcore.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static net.wuxianjie.springbootcore.util.StrUtils.equalsIgnoreBlank;
import static net.wuxianjie.springbootcore.util.StrUtils.toFuzzy;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@Slf4j
class StrUtilsTest {

  @Test
  @DisplayName("获取数据库 LIKE 字符串格式")
  void canGetFuzzySearchValue() {
    assertThat(toFuzzy(null)).isNull();
    assertThat(toFuzzy("")).isNull();
    assertThat(toFuzzy(" ")).isNull();
    assertThat(toFuzzy(" \t\n")).isNull();

    assertThat(toFuzzy("null")).isEqualTo("%null%");
    assertThat(toFuzzy("ja")).isEqualTo("%ja%");
    assertThat(toFuzzy(" ja")).isEqualTo("%ja%");
    assertThat(toFuzzy("ja ")).isEqualTo("%ja%");
    assertThat(toFuzzy(" ja ")).isEqualTo("%ja%");
  }

  @Test
  @DisplayName("判断字符串是否相等")
  void canEqualsIgnoreEmptyStr() {
    assertThat(equalsIgnoreBlank(null, null)).isTrue();
    assertThat(equalsIgnoreBlank(null, "")).isTrue();
    assertThat(equalsIgnoreBlank(null, " ")).isTrue();
    assertThat(equalsIgnoreBlank(null, " \t\n")).isTrue();
    assertThat(equalsIgnoreBlank("abc", "abc ")).isTrue();
    assertThat(equalsIgnoreBlank("abc", " abc")).isTrue();
    assertThat(equalsIgnoreBlank("abc", "abc ")).isTrue();
    assertThat(equalsIgnoreBlank("abc", " abc ")).isTrue();

    assertThat(equalsIgnoreBlank(null, "null")).isFalse();
    assertThat(equalsIgnoreBlank("abc", "abC")).isFalse();
  }
}