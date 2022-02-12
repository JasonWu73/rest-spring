package net.wuxianjie.web.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.model.CachedToken;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.model.PaginationQuery;
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
 * 实现操作日志的业务操作
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogServiceImpl implements OperationLogService {

  private final OperationLogMapper logMapper;
  private final AuthenticationFacade authentication;

  @Override
  public PaginationData<List<OperationLog>> getOperationLogs(
      @NonNull final PaginationQuery pagination,
      @NonNull final LocalDateTime startTime,
      @NonNull final LocalDateTime endTime) {
    // 根据分页条件、开始时间和结束时间从数据库中查询操作日志列表
    final List<OperationLog> logs = logMapper.getOperationLogs(pagination, startTime, endTime);

    // 根据开始时间和结束时间从数据库中统计操作日志总数
    final int total = logMapper.countOperationLogs(startTime, endTime);

    // 生成操作日志分页列表结果
    return new PaginationData<>(total, pagination.getPageNo(), pagination.getPageSize(), logs);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Wrote2Database saveOperationLog(@NonNull final LocalDateTime operationTime, @NonNull final String message) {
    // 获取当前用户信息
    final CachedToken cachedToken = authentication.loadCacheToken();
    final Integer userId = cachedToken.getAccountId();
    final String username = cachedToken.getAccountName();

    // 生成日志数据
    final LogToAdd logToAdd = new LogToAdd(userId, username, operationTime, message);

    // 入库
    final int addedNum = logMapper.saveOperationLog(logToAdd);

    return new Wrote2Database(addedNum, "新增日志成功");
  }

  @Data
  @AllArgsConstructor
  public static class LogToAdd {

    /** 本条记录的操作人用户ID */
    private Integer userId;

    /** 操作人用户名 */
    private String username;

    /** 本条记录的操作时间 */
    private LocalDateTime operationTime;

    /** 日志消息 */
    private String message;
  }
}
