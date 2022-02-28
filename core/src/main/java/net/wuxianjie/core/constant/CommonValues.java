package net.wuxianjie.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 常用值常量类
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonValues {

  /** 中国东八区时区 */
  public static final String CHINA_TIME_ZONE = "Asia/Shanghai";

  /** 仅包含日期的格式化字符串，比如：{@code 2022-01-25} */
  public static final String DATE_FORMAT = "yyyy-MM-dd";

  /** 同时包含日期和时间的格式化字符串，比如：{@code 2022-01-25 17:43:25} */
  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  /** JSON MIME */
  public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
}
