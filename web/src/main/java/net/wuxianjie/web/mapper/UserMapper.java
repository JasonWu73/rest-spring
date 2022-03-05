package net.wuxianjie.web.mapper;

import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.web.dto.UserDto;
import net.wuxianjie.web.model.User;
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
