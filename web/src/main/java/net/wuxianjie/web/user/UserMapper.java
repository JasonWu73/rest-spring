package net.wuxianjie.web.user;

import net.wuxianjie.springbootcore.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表相关。
 *
 * @author 吴仙杰
 */
@Mapper
public interface UserMapper {

  User selectUserById(@Param("id") int userId);

  User selectUserByName(String username);

  /**
   * 获取用户分页列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 用户分页列表
   */
  List<UserDto> findByUsernameLikeAndEnabled(@Param("p") PagingQuery paging,
                                             @Param("q") GetUserQuery query);

  /**
   * 统计用户数量。
   *
   * @param query 查询参数
   * @return 符合条件的用户总数
   */
  int countByUsernameLikeAndEnabled(@Param("q") GetUserQuery query);

  boolean existsUserByName(String username);

  void insertUser(User user);

  void updateUser(User user);

  void deleteUserById(@Param("id") int userId);
}
