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

    User findById(@Param("id") int userId);

    User findByName(String username);

    List<ListOfUserItem> findByQueryPagingOrderByModifyTimeDesc(@Param("p") PagingQuery paging,
                                                                @Param("q") GetUserQuery query);

    int countByQuery(@Param("q") GetUserQuery query);

    boolean existsUsername(String username);

    int add(User user);

    int update(User user);

    int deleteById(@Param("id") int userId);
}
