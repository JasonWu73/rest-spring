package net.wuxianjie.web.operationlog;

import net.wuxianjie.springbootcore.paging.RequestOfPaging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 与操作日志表相关的 SQL。
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
  List<OperationLog> findByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLikeOrderByOpTimeDesc(@Param("p") RequestOfPaging paging,
                                                                                                                    @Param("q") RequestOfGetOperationLog query);

  /**
   * 统计操作日志总数。
   *
   * @param query 查询参数
   * @return 符合条件的操作日志总数
   */
  int countByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLike(@Param("q") RequestOfGetOperationLog query);

  /**
   * 保存操作日志数据。
   *
   * @param logData 需要保存的操作日志数据
   */
  void save(OperationLog logData);
}
