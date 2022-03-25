package net.wuxianjie.springbootcore.shared;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static net.wuxianjie.springbootcore.shared.StringUtils.equalsIgnoreBlank;
import static net.wuxianjie.springbootcore.shared.StringUtils.getFuzzySearchValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class StringUtilsTest {

    @Test
    void getFuzzySearchValueShouldEqualsExpected() {
        assertAll("返回 null",
                () -> assertNull(getFuzzySearchValue(null)),
                () -> assertNull(getFuzzySearchValue("")),
                () -> assertNull(getFuzzySearchValue(" ")),
                () -> assertNull(getFuzzySearchValue(" \t\n"))
        );

        assertAll("返回 %str% 形式的字符串",
                () -> assertEquals("%null%", getFuzzySearchValue("null")),
                () -> assertEquals("%ja%", getFuzzySearchValue("ja")),
                () -> assertEquals("%ja%", getFuzzySearchValue(" ja")),
                () -> assertEquals("%ja%", getFuzzySearchValue("ja ")),
                () -> assertEquals("%ja%", getFuzzySearchValue(" ja "))
        );
    }

    @Test
    void equalsIgnoreEmptyShouldExpected() {
        assertAll("字符串值判断为相等的情况",
                () -> assertTrue(equalsIgnoreBlank(null, null)),
                () -> assertTrue(equalsIgnoreBlank(null, "")),
                () -> assertTrue(equalsIgnoreBlank(null, " ")),
                () -> assertTrue(equalsIgnoreBlank(null, " \t\n")),
                () -> assertTrue(equalsIgnoreBlank("abc", "abc ")),
                () -> assertTrue(equalsIgnoreBlank("abc", " abc")),
                () -> assertTrue(equalsIgnoreBlank("abc", "abc ")),
                () -> assertTrue(equalsIgnoreBlank("abc", " abc "))
        );

        assertAll("字符串值判断为不相等的情况",
                () -> assertFalse(equalsIgnoreBlank(null, "null")),
                () -> assertFalse(equalsIgnoreBlank("abc", "abC"))
        );
    }
}