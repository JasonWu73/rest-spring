package net.wuxianjie.web.service;

import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.model.PaginationQuery;
import net.wuxianjie.web.model.OperationLog;
import net.wuxianjie.web.model.Wrote2Database;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 关于操作日志相关的业务接口
 *
 * @author 吴仙杰
 */
public interface OperationLogService {

  /**
   * 根据分页及时间段获取操作日志列表数据
   *
   * @param pagination 分页条件，非空
   * @param startTime 开始日期（包含），非空
   * @param endTime 结束日期（包含），非空
   * @return 操作日志列表分页数据
   */
  PaginationData<List<OperationLog>> getOperationLogs(
      PaginationQuery pagination, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * 新增操作日志
   *
   * @param operationTime 操作的时间，非空
   * @param message 操作的详细说明，如新增/删除了什么、将什么修改为什么，非空
   * @return 数据库的新增结果
   */
  @SuppressWarnings("UnusedReturnValue")
  Wrote2Database saveOperationLog(LocalDateTime operationTime, String message);
}
