package net.wuxianjie.springbootcore.shared;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author 吴仙杰
 */
@Slf4j
class StringUtilsTest {

    @Test
    void getFuzzySearchValueShouldEqualsExpected() {
        Assertions.assertAll("返回 null",
                () -> Assertions.assertNull(StringUtils
                        .getFuzzySearchValue(null)),
                () -> Assertions.assertNull(StringUtils
                        .getFuzzySearchValue("")),
                () -> Assertions.assertNull(StringUtils
                        .getFuzzySearchValue(" ")),
                () -> Assertions.assertNull(StringUtils
                        .getFuzzySearchValue(" \t\n")));

        Assertions.assertAll("返回 %str% 形式的字符串",
                () -> Assertions.assertEquals(
                        "%null%", StringUtils.getFuzzySearchValue("null")),
                () -> Assertions.assertEquals(
                        "%ja%", StringUtils.getFuzzySearchValue("ja")),
                () -> Assertions.assertEquals(
                        "%ja%", StringUtils.getFuzzySearchValue(" ja")),
                () -> Assertions.assertEquals(
                        "%ja%", StringUtils.getFuzzySearchValue("ja ")),
                () -> Assertions.assertEquals(
                        "%ja%", StringUtils.getFuzzySearchValue(" ja ")));
    }

    @Test
    void equalsIgnoreEmptyShouldExpected() {
        Assertions.assertAll("字符串值判断为相等的情况",
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank(null, null)),
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank(null, "")),
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank(null, " ")),
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank(null, " \t\n")),
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank("abc", "abc ")),
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank("abc", " abc")),
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank("abc", "abc ")),
                () -> Assertions.assertTrue(
                        StringUtils.equalsIgnoreBlank("abc", " abc ")));

        Assertions.assertAll("字符串值判断为不相等的情况",
                () -> Assertions.assertFalse(
                        StringUtils.equalsIgnoreBlank(null, "null")),
                () -> Assertions.assertFalse(
                        StringUtils.equalsIgnoreBlank("abc", "abC")));
    }
}