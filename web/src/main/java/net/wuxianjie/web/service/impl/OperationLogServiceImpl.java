package net.wuxianjie.web.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.dto.PrincipalDto;
import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.core.service.AuthenticationFacade;
import net.wuxianjie.web.mapper.OperationLogMapper;
import net.wuxianjie.web.model.OperationLog;
import net.wuxianjie.web.model.Wrote2Database;
import net.wuxianjie.web.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 实现操作日志业务逻辑
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogServiceImpl implements OperationLogService {

  private final OperationLogMapper logMapper;
  private final AuthenticationFacade authentication;

  @Override
  public PaginationDto<List<OperationLog>> getOperationLogs(
      @NonNull final PaginationQueryDto pagination,
      @NonNull final LocalDateTime startTime,
      @NonNull final LocalDateTime endTime) {
    // 根据分页条件、开始时间和结束时间从数据库中查询操作日志列表
    final List<OperationLog> logs = logMapper.findByPagination(pagination, startTime, endTime);

    // 根据开始时间和结束时间从数据库中统计操作日志总数
    final int total = logMapper.countByTime(startTime, endTime);

    // 生成操作日志分页列表结果
    return new PaginationDto<>(total, pagination.getPageNo(), pagination.getPageSize(), logs);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database saveOperationLog(
      @NonNull final LocalDateTime operationTime, @NonNull final String message) {
    // 获取当前用户信息
    final PrincipalDto cachedToken = authentication.getCacheToken();
    final Integer userId = cachedToken.getAccountId();
    final String username = cachedToken.getAccountName();

    // 生成日志数据
    final LogToAdd logToAdd = new LogToAdd(userId, username, operationTime, message);

    // 将日志数据保存到数据库中
    final int addedNum = logMapper.add(logToAdd);

    return new Wrote2Database(addedNum, "新增日志成功");
  }

  @Data
  @AllArgsConstructor
  public static class LogToAdd {

    /** 用户ID，即操作人ID */
    private Integer userId;

    /** 用户名，即操作人名称 */
    private String username;

    /** 本条记录的操作时间 */
    private LocalDateTime operationTime;

    /** 操作的详细内容 */
    private String message;
  }
}
