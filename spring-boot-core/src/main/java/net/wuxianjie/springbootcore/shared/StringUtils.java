package net.wuxianjie.springbootcore.shared;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 字符串工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    /**
     * 去除字符串的首尾空格，并转换为支持数据库 LIKE 模糊搜索的字符串（{@code %value%}）；若 {@code value} 为 null 或仅包含空白字符，则返回 null。
     */
    public static String getFuzzySearchValue(String value) {
        String trimmedStr = StrUtil.trimToNull(value);
        if (trimmedStr == null) {
            return null;
        }
        return "%" + trimmedStr + "%";
    }

    /**
     * 忽略 null 后判断两个字符串值是否相等，即认为 null 和空字符串相等。例如：
     *
     * <ul>
     *     <li>StringUtils.isEqualsIgnoreNull(null, null)     // true</li>
     *     <li>StringUtils.isEqualsIgnoreNull(null, "")       // true</li>
     *     <li>StringUtils.isEqualsIgnoreNull("abc", "abc")    // true</li>
     *     <li>StringUtils.isEqualsIgnoreNull(null, " \t\n")  // false</li>
     *     <li>StringUtils.isEqualsIgnoreNull(null, " ")  // false</li>
     *     <li>StringUtils.isEqualsIgnoreNull("abc", "abc ")    // false</li>
     * </ul>
     */
    public static boolean isEqualsIgnoreNull(String valueOne, String valueTwo) {
        if (StrUtil.isEmpty(valueOne) && StrUtil.isEmpty(valueTwo)) {
            return true;
        }

        return Objects.equals(valueOne, valueTwo);
    }
}
