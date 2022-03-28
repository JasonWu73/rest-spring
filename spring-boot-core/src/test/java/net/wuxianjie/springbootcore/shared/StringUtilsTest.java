package net.wuxianjie.springbootcore.shared;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 吴仙杰
 */
@Slf4j
class StringUtilsTest {

    @Test
    void getFuzzySearchValueShouldEqualsExpected() {
        assertAll("返回 null",
                () -> assertNull(StringUtils.getFuzzySearchValue(null)),
                () -> assertNull(StringUtils.getFuzzySearchValue("")),
                () -> assertNull(StringUtils.getFuzzySearchValue(" ")),
                () -> assertNull(StringUtils.getFuzzySearchValue(" \t\n")));

        assertAll("返回 %str% 形式的字符串",
                () -> assertEquals("%null%", StringUtils.getFuzzySearchValue("null")),
                () -> assertEquals("%ja%", StringUtils.getFuzzySearchValue("ja")),
                () -> assertEquals("%ja%", StringUtils.getFuzzySearchValue(" ja")),
                () -> assertEquals("%ja%", StringUtils.getFuzzySearchValue("ja ")),
                () -> assertEquals("%ja%", StringUtils.getFuzzySearchValue(" ja ")));
    }

    @Test
    void equalsIgnoreEmptyShouldExpected() {
        assertAll("字符串值判断为相等的情况",
                () -> assertTrue(StringUtils.equalsIgnoreBlank(null, null)),
                () -> assertTrue(StringUtils.equalsIgnoreBlank(null, "")),
                () -> assertTrue(StringUtils.equalsIgnoreBlank(null, " ")),
                () -> assertTrue(StringUtils.equalsIgnoreBlank(null, " \t\n")),
                () -> assertTrue(StringUtils.equalsIgnoreBlank("abc", "abc ")),
                () -> assertTrue(StringUtils.equalsIgnoreBlank("abc", " abc")),
                () -> assertTrue(StringUtils.equalsIgnoreBlank("abc", "abc ")),
                () -> assertTrue(StringUtils.equalsIgnoreBlank("abc", " abc ")));

        assertAll("字符串值判断为不相等的情况",
                () -> assertFalse(StringUtils.equalsIgnoreBlank(null, "null")),
                () -> assertFalse(StringUtils.equalsIgnoreBlank("abc", "abC")));
    }
}