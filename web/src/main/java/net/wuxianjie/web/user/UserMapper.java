package net.wuxianjie.web.user;

import net.wuxianjie.core.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findById(@Param("id") int userId);

    User findUserByUsername(String username);

    List<User> findByUsernameLimitModifyTimeDesc(
            @Param("p") PagingQuery paging,
            @Param("username") String fuzzyUsername
    );

    int countByUsername(@Param("username") String fuzzyUsername);

    boolean existsUsername(String username);

    int add(User userToAdd);

    int update(User userToUpdate);

    int updatePasswordById(@Param("id") int userId, String hashedPassword);

    int deleteById(@Param("id") int userId);
}
