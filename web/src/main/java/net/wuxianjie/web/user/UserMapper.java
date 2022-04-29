package net.wuxianjie.web.user;

import net.wuxianjie.springbootcore.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表相关 SQL。
 *
 * @author 吴仙杰
 */
@Mapper
public interface UserMapper {

  /**
   * 通过用户 id 获取用户数据。
   *
   * @param userId 用户 id
   * @return 用户数据
   */
  User findByUserId(int userId);

  /**
   * 通过用户名获取用户数据。
   *
   * @param username 用户名
   * @return 用户数据
   */
  User findByUsername(String username);

  /**
   * 获取用户分页列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 用户分页列表
   */
  List<UserItemDto> findByUsernameLikeAndEnabledOrderByModifyTimeDesc(@Param("p") PagingQuery paging,
                                                                      @Param("q") GetUserQuery query);

  /**
   * 统计用户数量。
   *
   * @param query 查询参数
   * @return 符合条件的用户总数
   */
  int countByUsernameLikeAndEnabled(@Param("q") GetUserQuery query);

  /**
   * 检查是否已存在相同用户名。
   *
   * @param username 用户名
   * @return true：已存在，false：不存在
   */
  boolean existsByUsername(String username);

  /**
   * 保存用户数据。
   *
   * @param user 需要保存的用户数据
   */
  void save(User user);

  /**
   * 更新用户数据。
   *
   * @param user 需要更新的用户数据
   */
  void update(User user);

  /**
   * 删除用户。
   *
   * @param userId 用户 id
   */
  void deleteByUserId(int userId);
}
