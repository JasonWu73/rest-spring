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

    List<UserDto> selectUsers(@Param("p") PagingQuery paging,
                              @Param("q") UserQuery query);

    int countUsers(@Param("q") UserQuery query);

    boolean existsUserByName(String username);

    void insertUser(User user);

    void updateUser(User user);

    void deleteUserById(@Param("id") int userId);
}
