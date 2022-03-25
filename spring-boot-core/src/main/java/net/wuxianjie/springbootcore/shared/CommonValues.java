package net.wuxianjie.springbootcore.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 常见的常量值。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonValues {

    /**
     * 中国时区（东八区，上海）。
     */
    public static final String CHINA_TIME_ZONE = "Asia/Shanghai";

    /**
     * 仅包含日期的字符串格式。
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 同时包含日期和时间的字符串格式。
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * MIME 类型（Multipurpose Internet Mail Extensions，媒体类型）：包含 UTF-8 编码的 JSON。
     */
    public static final String APPLICATION_JSON_UTF8_VALUE =
            "application/json;charset=UTF-8";
}
