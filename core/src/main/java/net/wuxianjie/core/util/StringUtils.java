package net.wuxianjie.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
   * @return 若str为null，则返回null；若str为空白字符串，则返回空字符串；若str包含非空字符，则返回%str%形式字符串（已去除str的首尾空格）
   */
  public static String generateDbFuzzyStr(final String str) {
    if (str == null) {
      return null;
    }

    final String trimmed = str.trim();

    if (trimmed.length() == 0) {
      return str;
    }

    return "%" + trimmed + "%";
  }
}
