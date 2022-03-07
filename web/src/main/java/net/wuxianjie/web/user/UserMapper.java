package net.wuxianjie.web.user;

import net.wuxianjie.core.shared.pagination.PaginationQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findById(int userId);

    User findByUsername(String username);

    List<User> findByPagination(
            @Param("page") PaginationQueryDto pagination,
            @Param("username") String fuzzyUsername
    );

    int countByUsername(String fuzzyUsername);

    int add(UserDto userToAdd);

    int update(UserDto userToUpdate);

    int updatePasswordById(int userId, String hashedPassword);

    int deleteById(int userId);
}
