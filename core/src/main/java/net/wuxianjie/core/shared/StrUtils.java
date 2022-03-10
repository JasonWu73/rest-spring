package net.wuxianjie.core.shared;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StrUtils {

    /**
     * 去除字符串的首尾空白字符，并转换为支持数据库 LIKE 模糊搜索的字符串 {@code %str%}
     *
     * @param str 需要转换的字符串
     * @return 去除字符串首尾空白字符后形式为 {@code %str%} 的字符串，当 str 为 null 时则返回 null
     */
    @Nullable
    public static String generateDbFuzzyStr(String str) {
        final String trimmedStr = StrUtil.trimToNull(str);

        if (trimmedStr == null) {
            return null;
        }

        return "%" + trimmedStr + "%";
    }

    /**
     * 忽略 null 后判断两个字符串值是否相等，即 null 和空字符串相等。例如：
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
    public static boolean isEqualsIgnoreNull(String s1, String s2) {
        if (StrUtil.isEmpty(s1) && StrUtil.isEmpty(s2)) {
            return true;
        }

        return Objects.equals(s1, s2);
    }
}
