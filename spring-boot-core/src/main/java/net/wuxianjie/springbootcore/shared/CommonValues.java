package net.wuxianjie.springbootcore.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonValues {

  public static final String CHINA_TIME_ZONE = "Asia/Shanghai";

  public static final String DATE_FORMAT = "yyyy-MM-dd";

  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

  public static final String APPLICATION_JSON_UTF8_VALUE =
      "application/json;charset=UTF-8";
}
