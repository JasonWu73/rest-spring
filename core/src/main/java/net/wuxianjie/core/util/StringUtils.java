package net.wuxianjie.core.util;

import cn.hutool.core.util.StrUtil;
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
   *  先去除字符串的头尾空白字符，再将其转换为支持数据库模糊搜索字符串（{@code "%str%"}）
   *
   * @param str 需要转换的字符串
   * @return 若str为空白字符串，则返回空字符串（去除空白）；若str为null，则返回null；若str包含非空字符，则返回去除头尾空白后字符串的%str%形式字符串
   */
  public static String generateDbFuzzyStr(final String str) {
    final String trimmed = StrUtil.trim(str);

    if (StrUtil.isEmpty(trimmed)) {
      return str;
    }

    return "%" + trimmed + "%";
  }
}
