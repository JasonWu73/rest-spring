package net.wuxianjie.web.operationlog;

import net.wuxianjie.springbootcore.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志表相关。
 *
 * @author 吴仙杰
 */
@Mapper
public interface OperationLogMapper {

  /**
   * 获取操作日志分页列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 操作日志分页列表
   */
  List<LogItemDto> findByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLikeOrderByOperationTimeDesc(@Param("p") PagingQuery paging,
                                                                                                                         @Param("q") GetLogQuery query);

  /**
   * 统计操作日志数量。
   *
   * @param query 查询参数
   * @return 符合条件的操作日志总数
   */
  int countByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLike(@Param("q") GetLogQuery query);

  void insertLog(OperationLog log);
}
