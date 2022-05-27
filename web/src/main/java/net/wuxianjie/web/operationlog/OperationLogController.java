package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.RequestOfPaging;
import net.wuxianjie.springbootcore.paging.ResultOfPaging;
import net.wuxianjie.springbootcore.util.StringUtils;
import net.wuxianjie.springbootcore.util.ParameterUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 操作日志 API 控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

  private final OperationLogService operationLogService;

  /**
   * 获取操作日志列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 操作日志列表
   */
  @GetMapping("list")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_OP_LOG.name())")
  public ResultOfPaging<OperationLog> getOperationLogs(@Valid RequestOfPaging paging,
                                                       @Valid RequestOfGetOperationLog query) {
    setFuzzySearchValue(query);
    setStartAndEndTime(query);
    return operationLogService.getOpLogs(paging, query);
  }

  private void setFuzzySearchValue(RequestOfGetOperationLog query) {
    query.setUsername(StringUtils.toNullableFuzzyString(query.getUsername()));
    query.setRequestIp(StringUtils.toNullableFuzzyString(query.getRequestIp()));
    query.setMethodMessage(StringUtils.toNullableFuzzyString(query.getMethodMessage()));
  }

  private void setStartAndEndTime(RequestOfGetOperationLog query) {
    LocalDateTime startTime = ParameterUtils.toNullableStartTime(query.getStartDate(), "开始日期不合法");
    query.setStartTimeInclusive(startTime);

    LocalDateTime endTime = ParameterUtils.toNullableEndTime(query.getEndDate(), "结束日期不合法");
    query.setEndTimeInclusive(endTime);

    ParameterUtils.checkForStartTimeIsBeforeEndTime(startTime, endTime, "开始日期不能晚于结束日期");
  }
}
