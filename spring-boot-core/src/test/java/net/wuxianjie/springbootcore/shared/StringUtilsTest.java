package net.wuxianjie.springbootcore.shared;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@Slf4j
class StringUtilsTest {

    @Test
    @DisplayName("获取数据库 LIKE 字符串格式")
    void canGetFuzzySearchValue() {
        assertThat(StringUtils.getFuzzySearchValue(null)).isNull();
        assertThat(StringUtils.getFuzzySearchValue("")).isNull();
        assertThat(StringUtils.getFuzzySearchValue(" ")).isNull();
        assertThat(StringUtils.getFuzzySearchValue(" \t\n")).isNull();


        assertThat(StringUtils.getFuzzySearchValue("null")).isEqualTo("%null%");
        assertThat(StringUtils.getFuzzySearchValue("ja")).isEqualTo("%ja%");
        assertThat(StringUtils.getFuzzySearchValue(" ja")).isEqualTo("%ja%");
        assertThat(StringUtils.getFuzzySearchValue("ja ")).isEqualTo("%ja%");
        assertThat(StringUtils.getFuzzySearchValue(" ja ")).isEqualTo("%ja%");
    }

    @Test
    @DisplayName("判断字符串是否相等")
    void canEqualsIgnoreEmptyStr() {
        assertThat(StringUtils.equalsIgnoreBlank(null, null)).isTrue();
        assertThat(StringUtils.equalsIgnoreBlank(null, "")).isTrue();
        assertThat(StringUtils.equalsIgnoreBlank(null, " ")).isTrue();
        assertThat(StringUtils.equalsIgnoreBlank(null, " \t\n")).isTrue();
        assertThat(StringUtils.equalsIgnoreBlank("abc", "abc ")).isTrue();
        assertThat(StringUtils.equalsIgnoreBlank("abc", " abc")).isTrue();
        assertThat(StringUtils.equalsIgnoreBlank("abc", "abc ")).isTrue();
        assertThat(StringUtils.equalsIgnoreBlank("abc", " abc ")).isTrue();

        assertThat(StringUtils.equalsIgnoreBlank(null, "null")).isFalse();
        assertThat(StringUtils.equalsIgnoreBlank("abc", "abC")).isFalse();
    }
}