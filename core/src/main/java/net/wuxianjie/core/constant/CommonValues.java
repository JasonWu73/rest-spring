package net.wuxianjie.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonValues {

    /**
     * 中国东八区时区
     */
    public static final String CHINA_TIME_ZONE = "Asia/Shanghai";

    /**
     * 仅包含日期的格式化字符串，比如：2022-03-05
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 同时包含日期和时间的格式化字符串，比如：2022-03-05 16:30:25
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * JSON MIME，用于 Content-Type HTTP Response Header
     */
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
}
