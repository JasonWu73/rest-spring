package net.wuxianjie.web.loginlog;

import net.wuxianjie.springbootcore.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 与登录日志表相关的 SQL。
 *
 * @author 吴仙杰
 */
@Mapper
public interface LoginLogMapper {

  /**
   * 获取登录日志分页列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 登录日志分页列表
   */
  List<LoginLog> findByLoginTimeBetweenAndUsernameLikeAndReqIpLikeOrderByLoginTimeDesc(@Param("p") PagingQuery paging,
                                                                                       @Param("q") GetLoginLogQuery query);

  /**
   * 统计登录日志的总记录数。
   *
   * @param query 查询参数
   * @return 符合条件的登录日志总数
   */
  int countByLoginTimeBetweenAndUsernameLikeAndReqIpLike(@Param("q") GetLoginLogQuery query);

  /**
   * 保存登录日志。
   *
   * @param logData 需要保存的数据
   */
  void save(LoginLog logData);
}
