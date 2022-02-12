package net.wuxianjie.web.mapper;

import net.wuxianjie.core.model.PaginationQuery;
import net.wuxianjie.web.model.OperationLog;
import net.wuxianjie.web.service.impl.OperationLogServiceImpl;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志数据的SQL映射器
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/mybatis">Quick Guide to MyBatis | Baeldung</a>
 */
@Mapper
public interface OperationLogMapper {

  /**
   * 根据分页条件、开始时间和结束时间从数据库中查询操作日志列表
   *
   * @param pagination 分页条件，非空
   * @param startTime 开始日期（包含），非空
   * @param endTime 结束日期（包含），非空
   * @return 操作日志列表分页数据
   */
  List<OperationLog> getOperationLogs(PaginationQuery pagination, LocalDateTime startTime, LocalDateTime endTime);

  /**
   * 根据开始时间和结束时间从数据库中统计操作日志总数
   *
   * @param startTime 开始日期（包含）
   * @param endTime 结束日期（包含）
   * @return 操作日志总数
   */
  int countOperationLogs(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * 新增操作日志
   *
   * @param logToAdd 需要保存的操作日志
   * @return 新增行数
   */
  int saveOperationLog(OperationLogServiceImpl.LogToAdd logToAdd);
}
