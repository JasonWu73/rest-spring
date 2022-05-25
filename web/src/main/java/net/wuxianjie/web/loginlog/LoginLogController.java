package net.wuxianjie.web.loginlog;

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
 * 登录日志的 API 控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/login-log")
@RequiredArgsConstructor
public class LoginLogController {

  private final LoginLogService loginLogService;

  /**
   * 获取登录日志列表。
   *
   * @param paging 分页参数
   * @param query  请求参数
   * @return 登录日志列表
   */
  @GetMapping("list")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysRole).ROLE_LOGIN_LOG.name())")
  public PagingResult<LoginLog> getLoginLogs(@Valid PagingQuery paging,
                                             @Valid GetLoginLogQuery query) {
    setFuzzySearchValue(query);
    setStartAndEndTime(query);
    return loginLogService.getLoginLogs(paging, query);
  }

  private void setFuzzySearchValue(GetLoginLogQuery query) {
    query.setUsername(StrUtils.toFuzzy(query.getUsername()));
    query.setReqIp(StrUtils.toFuzzy(query.getReqIp()));
  }

  private void setStartAndEndTime(GetLoginLogQuery query) {
    LocalDateTime startTime = ParamUtils.toNullableStartTime(query.getStartDate(), "开始日期不合法");
    query.setStartTimeInclusive(startTime);

    LocalDateTime endTime = ParamUtils.toNullableEndTime(query.getEndDate(), "结束日期不合法");
    query.setEndTimeInclusive(endTime);

    ParamUtils.verifyStartTimeIsBeforeEndTime(startTime, endTime, "开始日期不能晚于结束日期");
  }
}
