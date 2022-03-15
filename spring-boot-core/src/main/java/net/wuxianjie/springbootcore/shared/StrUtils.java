package net.wuxianjie.springbootcore.shared;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StrUtils {

  /**
   * 去除字符串的首尾空格，并转换为支持数据库 LIKE 模糊搜索的字符串 {@code %str%}
   *
   * @param str 需要转换的字符串
   * @return 当 str 为 null 时，则返回 null，
   *         否则返回去除字符串首尾空格后的 {@code %str%} 形式字符串
   */
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
   *   <li>StringUtils.isEqualsIgnoreNull(null, null)     // true</li>
   *   <li>StringUtils.isEqualsIgnoreNull(null, "")       // true</li>
   *   <li>StringUtils.isEqualsIgnoreNull("abc", "abc")    // true</li>
   *   <li>StringUtils.isEqualsIgnoreNull(null, " \t\n")  // false</li>
   *   <li>StringUtils.isEqualsIgnoreNull(null, " ")  // false</li>
   *   <li>StringUtils.isEqualsIgnoreNull("abc", "abc ")    // false</li>
   * </ul>
   *
   * @param s1 用于比较的字符串
   * @param s2 用于比较的字符串
   * @return 当且仅当两个字符串值（将空字符串等同于 null）相等才返回 {@code true}
   */
  public static boolean isEqualsIgnoreNull(String s1, String s2) {
    if (StrUtil.isEmpty(s1) && StrUtil.isEmpty(s2)) {
      return true;
    }

    return Objects.equals(s1, s2);
  }
}
