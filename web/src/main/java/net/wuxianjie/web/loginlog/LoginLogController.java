package net.wuxianjie.web.loginlog;

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
 * 登录日志 API 控制器。
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
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_LOGIN_LOG.name())")
  public ResultOfPaging<LoginLog> getLoginLogs(@Valid RequestOfPaging paging,
                                               @Valid RequestOfGetLoginLog query) {
    setFuzzySearchValue(query);
    setStartAndEndTime(query);
    return loginLogService.getLoginLogs(paging, query);
  }

  private void setFuzzySearchValue(RequestOfGetLoginLog query) {
    query.setUsername(StringUtils.toNullableFuzzyString(query.getUsername()));
    query.setRequestIp(StringUtils.toNullableFuzzyString(query.getRequestIp()));
  }

  private void setStartAndEndTime(RequestOfGetLoginLog query) {
    LocalDateTime startTime = ParameterUtils.toNullableStartTime(query.getStartDate(), "开始日期不合法");
    query.setStartTimeInclusive(startTime);

    LocalDateTime endTime = ParameterUtils.toNullableEndTime(query.getEndDate(), "结束日期不合法");
    query.setEndTimeInclusive(endTime);

    ParameterUtils.checkForStartTimeIsBeforeEndTime(startTime, endTime, "开始日期不能晚于结束日期");
  }
}
