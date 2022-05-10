package net.wuxianjie.web.shared;

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
public class ParamUtils {

  /**
   * 日期字符串转为该日期的开始时间。
   *
   * @param dateStr 格式为 yyyy-MM-dd 的日期字符串
   * @param errMsg 日期字符串解析失败时的提示信息
   * @return 日期时间，若 {@code dateStr} 为空，则返回 null
   */
  public static LocalDateTime toStartTimeOfDay(String dateStr, String errMsg) {
    if (StrUtil.isEmpty(dateStr)) {
      return null;
    }

    LocalDate startDate;

    try {
      startDate = LocalDate.parse(dateStr);
    } catch (DateTimeParseException e) {
      throw new BadRequestException(errMsg, e);
    }

    return startDate.atStartOfDay();
  }

  /**
   * 日期字符串转为该日期的结束时间。
   *
   * @param dateStr 格式为 yyyy-MM-dd 的日期字符串
   * @param errMsg 日期字符串解析失败时的提示信息
   * @return 日期时间，若 {@code dateStr} 为空，则返回 null
   */
  public static LocalDateTime toEndTimeOfDay(String dateStr, String errMsg) {
    if (StrUtil.isEmpty(dateStr)) {
      return null;
    }

    LocalDate endDate;

    try {
      endDate = LocalDate.parse(dateStr);
    } catch (DateTimeParseException e) {
      throw new BadRequestException(errMsg, e);
    }

    return endDate.atTime(LocalTime.MAX);
  }

  /**
   * 检查开始必须在结束时间之间。
   *
   * @param start 开始时间
   * @param end 结束时间
   */
  public static void verifyStartTimeIsBeforeEndTime(LocalDateTime start, LocalDateTime end) {
    if (start != null && end != null && start.isAfter(end)) {
      throw new BadRequestException("开始日期不能晚于结束日期");
    }
  }
}
