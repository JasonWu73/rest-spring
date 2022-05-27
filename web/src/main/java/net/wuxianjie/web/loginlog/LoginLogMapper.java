package net.wuxianjie.web.loginlog;

import net.wuxianjie.springbootcore.paging.RequestOfPaging;
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
  List<LoginLog> findByLoginTimeBetweenAndUsernameLikeAndRequestIpLikeOrderByLoginTimeDesc(@Param("p") RequestOfPaging paging,
                                                                                           @Param("q") RequestOfGetLoginLog query);

  /**
   * 统计登录日志总数。
   *
   * @param query 查询参数
   * @return 符合条件的登录日志总数
   */
  int countByLoginTimeBetweenAndUsernameLikeAndRequestIpLike(@Param("q") RequestOfGetLoginLog query);

  /**
   * 保存登录日志数据。
   *
   * @param logData 需要保存的操作日志数据
   */
  void save(LoginLog logData);
}
