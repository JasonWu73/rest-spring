package net.wuxianjie.springbootcore.util;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * 字符串工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StrUtils {

  /**
   * 去除字符串的首尾空白字符，并转换为支持数据库 LIKE 模糊搜索的字符串（{@code %value%}）。
   *
   * @param value 需要转换的原字符串
   * @return 去除字符串首尾空白字符后的 {@code %value%} 字符串；若 {@code value} 为 null 或仅包含空白字符，则返回 null
   */
  public static String toFuzzy(String value) {
    return Optional.ofNullable(StrUtil.trimToNull(value))
      .map(v -> "%" + v + "%")
      .orElse(null);
  }

  /**
   * 认为 null 、空字符串和仅包含空白字符的字符串都相等。例如：
   *
   * <ul>
   *     <li>{@code StringUtils.equalsIgnoreEmpty(null, null)     // true}</li>
   *     <li>{@code StringUtils.equalsIgnoreEmpty(null, "")       // true}</li>
   *     <li>{@code StringUtils.equalsIgnoreEmpty(null, " ")  // true}</li>
   *     <li>{@code StringUtils.equalsIgnoreEmpty(null, " \t\n")  // true}</li>
   *     <li>{@code StringUtils.equalsIgnoreEmpty("abc", "abc")    // true}</li>
   *     <li>{@code StringUtils.equalsIgnoreEmpty("abc", "abc ")    // true}</li>
   *     <li>{@code StringUtils.equalsIgnoreEmpty("abc", "abC")    // false}</li>
   * </ul>
   *
   * @param valueOne 需要比较的字符串
   * @param valueTwo 需要比较的字符串
   * @return 忽略 null 后，若两个字符串值相等，则返回 true；否则返回 false
   */
  public static boolean equalsIgnoreBlank(String valueOne, String valueTwo) {
    String trimmedOne = StrUtil.trimToNull(valueOne);
    String trimmedTwo = StrUtil.trimToNull(valueTwo);

    return StrUtil.equals(trimmedOne, trimmedTwo);
  }
}
