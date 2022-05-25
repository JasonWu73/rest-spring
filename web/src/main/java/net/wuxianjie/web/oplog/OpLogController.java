package net.wuxianjie.web.oplog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.util.StrUtils;
import net.wuxianjie.springbootcore.util.ParamUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * 操作日志的 API 控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/op-log")
@RequiredArgsConstructor
public class OpLogController {

  private final OpLogService logService;

  /**
   * 获取操作日志列表。
   *
   * @param paging 分页参数
   * @param query  请求参数
   * @return 操作日志列表
   */
  @GetMapping("list")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysRole).ROLE_OP_LOG.name())")
  public PagingResult<OpLog> getLogs(@Valid PagingQuery paging,
                                     @Valid GetOpLogQuery query) {
    setFuzzySearchValue(query);
    setSTartAndEndTime(query);
    return logService.getOpLogs(paging, query);
  }

  private void setFuzzySearchValue(GetOpLogQuery query) {
    query.setUsername(StrUtils.toFuzzy(query.getUsername()));
    query.setReqIp(StrUtils.toFuzzy(query.getReqIp()));
    query.setMethodMsg(StrUtils.toFuzzy(query.getMethodMsg()));
  }

  private void setSTartAndEndTime(GetOpLogQuery query) {
    LocalDateTime startTime = ParamUtils.toNullableStartTime(query.getStartDate(), "开始日期不合法");
    query.setStartTimeInclusive(startTime);

    LocalDateTime endTime = ParamUtils.toNullableEndTime(query.getEndDate(), "结束日期不合法");
    query.setEndTimeInclusive(endTime);

    ParamUtils.verifyStartTimeIsBeforeEndTime(startTime, endTime, "开始日期不能晚于结束日期");
  }
}
