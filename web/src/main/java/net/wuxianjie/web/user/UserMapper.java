package net.wuxianjie.web.user;

import net.wuxianjie.core.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

  User findById(@Param("id") int userId);

  User findUserByUsername(String username);

  List<User> findByQueryPagingModifyTimeDesc(@Param("p") PagingQuery paging,
                                             @Param("q")
                                                 ManagementOfUser query);

  int countByQuery(ManagementOfUser query);

  boolean existsUsername(String username);

  int add(User user);

  int update(User user);

  int deleteById(@Param("id") int userId);
}
