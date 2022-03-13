package net.wuxianjie.web.user;

import net.wuxianjie.core.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@Mapper
public interface UserMapper {

  @Nullable
  User findById(@Param("id") int userId);

  @Nullable
  User findUserByUsername(String username);

  @NonNull
  List<User> findByQueryPagingModifyTimeDesc(@Param("p") PagingQuery paging,
                                             @Param("q") ManagementOfUser query
  );

  int countByQuery(ManagementOfUser query);

  boolean existsUsername(String username);

  int add(User user);

  int update(User user);

  int deleteById(@Param("id") int userId);
}
