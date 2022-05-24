package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.util.StrUtils;
import net.wuxianjie.web.shared.ParamUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 操作日志控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

  private final OperationLogServiceImpl logService;

  /**
   * 获取操作日志列表。
   *
   * @param paging 分页参数
   * @param query  请求参数
   * @return 操作日志列表
   */
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysRole).ROLE_OP_LOG.name())")
  @GetMapping("list")
  public PagingResult<LogItemDto> getLogs(@Valid PagingQuery paging,
                                          @Valid GetLogQuery query) {
    setFuzzySearchValue(query);

    LocalDateTime startTime = ParamUtils.toStartTimeOfDay(query.getStartDate(), "开始日期不合法");
    query.setStartTimeInclusive(startTime);

    LocalDateTime endTime = ParamUtils.toEndTimeOfDay(query.getEndDate(), "结束日期不合法");
    query.setEndTimeInclusive(endTime);

    ParamUtils.verifyStartTimeIsBeforeEndTime(startTime, endTime);

    return logService.getLogs(paging, query);
  }

  private void setFuzzySearchValue(GetLogQuery query) {
    query.setUsername(StrUtils.toFuzzy(query.getUsername()));
    query.setRequestIp(StrUtils.toFuzzy(query.getRequestIp()));
    query.setMethodMessage(StrUtils.toFuzzy(query.getMethodMessage()));
  }
}
