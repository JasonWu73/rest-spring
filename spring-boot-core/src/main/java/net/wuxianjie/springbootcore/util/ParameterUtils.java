package net.wuxianjie.springbootcore.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * 参数处理工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParameterUtils {

  /**
   * 将日期字符串转为相应日期的开始时间。
   *
   * @param dateStr 格式为 yyyy-MM-dd 的日期字符串
   * @param errMsg  当日期字符串解析失败时抛出异常的提示信息
   * @return 日期的开始时间，若 {@code dateStr} 为空，则返回 null
   * @throws BadRequestException 当日期字符串解析失败时抛出
   */
  public static LocalDateTime toNullableStartTime(String dateStr, String errMsg) throws BadRequestException {
    if (StrUtil.isEmpty(dateStr)) return null;

    LocalDate startDate;
    try {
      startDate = LocalDate.parse(dateStr);
    } catch (DateTimeParseException e) {
      throw new BadRequestException(errMsg, e);
    }

    return startDate.atStartOfDay();
  }

  /**
   * 将日期字符串转为相应日期的结束时间。
   *
   * @param dateStr 格式为 yyyy-MM-dd 的日期字符串
   * @param errMsg  当日期字符串解析失败时抛出异常的提示信息
   * @return 日期的结束时间，若 {@code dateStr} 为空，则返回 null
   * @throws BadRequestException 当日期字符串解析失败时抛出
   */
  public static LocalDateTime toNullableEndTime(String dateStr, String errMsg) throws BadRequestException {
    if (StrUtil.isEmpty(dateStr)) return null;

    LocalDate endDate;
    try {
      endDate = LocalDate.parse(dateStr);
    } catch (DateTimeParseException e) {
      throw new BadRequestException(errMsg, e);
    }

    return endDate.atTime(LocalTime.MAX);
  }

  /**
   * 将日期时间字符串转为相应的日期时间对象。
   *
   * @param dateStr 格式为 yyyy-MM-dd HH:mm:ss 的日期时间字符串
   * @param errMsg  当日期时间字符串解析失败时抛出异常的提示信息
   * @return 日期时间对象，若 {@code dateStr} 为空，则返回 null
   * @throws BadRequestException 当日期时间字符串解析失败时抛出
   */
  public static LocalDateTime toNullableLocalDateTime(String dateStr, String errMsg) throws BadRequestException {
    if (StrUtil.isEmpty(dateStr)) return null;

    try {
      return LocalDateTimeUtil.parse(dateStr, DatePattern.NORM_DATETIME_PATTERN);
    } catch (DateTimeParseException e) {
      throw new BadRequestException(errMsg, e);
    }
  }

  /**
   * 验证开始时间必须在结束时间之前。
   *
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @param errMsg    当开始时间晚于结束时间时抛出异常的提示信息
   * @throws BadRequestException 当开始时间晚于结束时间时抛出
   */
  public static void checkForStartTimeIsBeforeEndTime(LocalDateTime startTime,
                                                      LocalDateTime endTime,
                                                      String errMsg) throws BadRequestException {
    if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
      throw new BadRequestException(errMsg);
    }
  }
}
