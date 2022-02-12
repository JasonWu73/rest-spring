package net.wuxianjie.web.controller;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.model.PaginationQuery;
import net.wuxianjie.web.model.OperationLog;
import net.wuxianjie.web.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 关于操作日志相关的REST API控制器
 *
 * @author 吴仙杰
 */
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogController {

  private final OperationLogService logService;


  /**
   * 根据分页条件及时间段获取操作日志列表数据
   *
   * @param pagination 分页条件，非空
   * @param startDate 开始日期（包含），非空，格式为yyyy-MM-dd
   * @param endDate 结束日期（包含），非空，格式为yyyy-MM-dd
   * @return 操作日志列表分页数据
   */
  @Admin
  @GetMapping("/logs")
  public PaginationData<List<OperationLog>> getOperationLogs(
      @Valid
      final PaginationQuery pagination,
      @NotNull(message = "开始日期不能为空")
      @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "开始日期格式错误")
      final String startDate,
      @NotNull(message = "结束日期不能为空")
      @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "结束日期格式错误")
      final String endDate) {
    // 从日期字符串解析得到日期对象
    final LocalDate startLocalDate = LocalDate.parse(startDate);
    final LocalDate endLocalDate = LocalDate.parse(endDate);

    // 若开始日期在结束日期之后，则直接退出方法
    if (startLocalDate.isAfter(endLocalDate)) {
      throw new BadRequestException("开始日期不能晚于结束日期");
    }

    // 分别获取开始日期的一天开始时间和结束日期的一天结束时间
    final LocalDateTime startTime = startLocalDate.atStartOfDay();
    final LocalDateTime endTime = endLocalDate.atTime(LocalTime.MAX);

    // 完善分页条件
    pagination.setOffset();

    // 根据分页条件、开始时间和结束时间获取操作日志列表
    return logService.getOperationLogs(pagination, startTime, endTime);
  }
}
