package net.wuxianjie.core.util;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 字符口中工具类
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

  /**
   *  去除字符串的首尾空白字符，再将其转换为支持数据库模糊搜索的字符串（{@code "%str%"}）
   *
   * @param str 需要转换的字符串
   * @return 若str为null或为空白字符串，则返回null；若str包含非空字符，则返回%str%形式字符串（已去除str的首尾空格）
   */
  public static String generateDbFuzzyStr(final String str) {
    // 除去字符串头尾部的空白，如果字符串是null或者""，返回null
    final String trimmed = StrUtil.trimToNull(str);

    if (trimmed == null) {
      return null;
    }

    // 构造用于数据库LIKE的模糊查询值
    return "%" + trimmed + "%";
  }

  /**
   * 忽略{@code null}判断两个字符串值是否相等，即认为{@code null}和空字符串相等
   *
   * <p>例：</p>
   * <ul>
   *     <li>{@code StringUtils.isEqualsIgnoreNull(null, null)     // true}</li>
   *     <li>{@code StringUtils.isEqualsIgnoreNull(null, "")       // true}</li>
   *     <li>{@code StringUtils.isEqualsIgnoreNull("abc", "abc")    // true}</li>
   *     <li>{@code StringUtils.isEqualsIgnoreNull(null, " \t\n")  // false}</li>
   *     <li>{@code StringUtils.isEqualsIgnoreNull(null, " ")  // false}</li>
   *     <li>{@code StringUtils.isEqualsIgnoreNull("abc", "abc ")    // false}</li>
   * </ul>
   *
   * @param s1 用于比较的字符串
   * @param s2 用于比较的字符串
   * @return 若两个字符串值相等则返回true，否则返回false
   */
  public static boolean isNullEquals(final String s1, final String s2) {
    if (StrUtil.isEmpty(s1) && StrUtil.isEmpty(s2)) {
      return true;
    }

    return Objects.equals(s1, s2);
  }
}
