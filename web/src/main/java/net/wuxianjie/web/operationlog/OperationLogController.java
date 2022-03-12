package net.wuxianjie.web.operationlog;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.paging.PagingData;
import net.wuxianjie.core.paging.PagingQuery;
import net.wuxianjie.core.security.Admin;
import net.wuxianjie.core.shared.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 操作日志。
 */
@Validated
@RestController
@RequestMapping("/api/v1/operation-log")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogController {

  private final OperationLogService logService;

  /**
   * 获取操作日志列表。
   */
  @Admin
  @GetMapping("list")
  public PagingData<List<ListItemOfOperationLog>> getOperationLogs(
    @Validated PagingQuery paging,

    @Pattern(message = "开始日期不符合 yyyy-MM-dd 格式",
      regexp = "(^$|^\\d{4}-\\d{2}-\\d{2}$)"
    ) String startDate,

    @Pattern(message = "结束日期不符合 yyyy-MM-dd 格式",
      regexp = "(^$|^\\d{4}-\\d{2}-\\d{2}$)"
    ) String endDate
  ) {
    final LocalDateTime startTimeInclusive = getStartTime(startDate);

    final LocalDateTime endTimeInclusive = getEndTime(endDate);

    return logService.getOperationLogs(
      paging,
      startTimeInclusive,
      endTimeInclusive
    );
  }

  @Nullable
  private LocalDateTime getStartTime(String startDateStr) {
    if (StrUtil.isEmpty(startDateStr)) {
      return null;
    }

    final LocalDate startDate;

    try {
      startDate = LocalDate.parse(startDateStr);
    } catch (DateTimeParseException e) {
      throw new BadRequestException("开始日期错误", e);
    }

    return startDate.atStartOfDay();
  }

  @Nullable
  private LocalDateTime getEndTime(String endDateStr) {
    if (StrUtil.isEmpty(endDateStr)) {
      return null;
    }

    final LocalDate endDate;

    try {
      endDate = LocalDate.parse(endDateStr);
    } catch (DateTimeParseException e) {
      throw new BadRequestException("结束日期错误", e);
    }

    return endDate.atTime(LocalTime.MAX);
  }
}
