package net.wuxianjie.web.service;

import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.web.model.OperationLog;
import net.wuxianjie.web.model.Wrote2Database;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志业务逻辑接口
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
  PaginationDto<List<OperationLog>> getOperationLogs(
          PaginationQueryDto pagination, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * 新增操作日志
   *
   * @param operationTime 操作的时间，非空
   * @param message 操作的详细说明，非空。需指明具体操作内容，如新增/删除了什么、将什么修改为什么
   * @return 数据库的写入情况及说明
   */
  @SuppressWarnings("UnusedReturnValue")
  Wrote2Database saveOperationLog(LocalDateTime operationTime, String message);
}
