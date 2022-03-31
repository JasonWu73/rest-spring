package net.wuxianjie.springbootcore.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static net.wuxianjie.springbootcore.shared.util.StringUtils.equalsIgnoreBlank;
import static net.wuxianjie.springbootcore.shared.util.StringUtils.getFuzzySearchValue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@Slf4j
class StringUtilsTest {

    @Test
    @DisplayName("获取数据库 LIKE 字符串格式")
    void canGetFuzzySearchValue() {
        assertThat(getFuzzySearchValue(null)).isNull();
        assertThat(getFuzzySearchValue("")).isNull();
        assertThat(getFuzzySearchValue(" ")).isNull();
        assertThat(getFuzzySearchValue(" \t\n")).isNull();

        assertThat(getFuzzySearchValue("null")).isEqualTo("%null%");
        assertThat(getFuzzySearchValue("ja")).isEqualTo("%ja%");
        assertThat(getFuzzySearchValue(" ja")).isEqualTo("%ja%");
        assertThat(getFuzzySearchValue("ja ")).isEqualTo("%ja%");
        assertThat(getFuzzySearchValue(" ja ")).isEqualTo("%ja%");
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