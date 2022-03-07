package net.wuxianjie.core.shared.util;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    /**
     * 去除字符串的首尾空白字符，
     * 再将其转换为支持数据库 LIKE 模糊搜索的字符串（{@code "%str%"}）
     *
     * @param str 需要转换的字符串
     * @return 若 {@code str} 为 {@code null} 或为空白字符串，则返回 {@code null}；
     * 否则返回 {@code %str%} 形式字符串（已去除 {@code str} 的首尾空白字符）
     */
    public static String generateDbFuzzyStr(final String str) {
        final String trimmed = StrUtil.trimToNull(str);

        if (trimmed == null) {
            return null;
        }

        return "%" + trimmed + "%";
    }

    /**
     * 忽略 {@code null} 判断两个字符串值是否相等，
     * 即认为 {@code null} 和空字符串相等。例如：
     *
     * <ul>
     *     <li>StringUtils.isEqualsIgnoreNull(null, null)     // true</li>
     *     <li>StringUtils.isEqualsIgnoreNull(null, "")       // true</li>
     *     <li>StringUtils.isEqualsIgnoreNull("abc", "abc")    // true</li>
     *     <li>StringUtils.isEqualsIgnoreNull(null, " \t\n")  // false</li>
     *     <li>StringUtils.isEqualsIgnoreNull(null, " ")  // false</li>
     *     <li>StringUtils.isEqualsIgnoreNull("abc", "abc ")    // false</li>
     * </ul>
     *
     * @param s1 用于比较的字符串
     * @param s2 用于比较的字符串
     * @return 若两个字符串值相等则返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isNullEquals(final String s1, final String s2) {
        if (StrUtil.isEmpty(s1) && StrUtil.isEmpty(s2)) {
            return true;
        }

        return Objects.equals(s1, s2);
    }
}
