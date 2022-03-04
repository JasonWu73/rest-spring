package net.wuxianjie.web.mapper;

import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.web.controller.UserController;
import net.wuxianjie.web.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    User findById(int userId);

    User findByUsername(String username);

    /**
     * 根据分页条件及用户名从数据库中获取用户列表数据
     *
     * @param pagination    分页条件，非空
     * @param fuzzyUsername 支持数据库模糊查询的用户名，null代表不使用该查询条件
     * @return 用户列表分页数据
     */
    List<User> findByPagination(@Param("page") PaginationQueryDto pagination,
                                @Param("username") String fuzzyUsername);

    /**
     * 根据用户名从数据库中统计用户总数
     *
     * @param fuzzyUsername 支持数据库模糊查询的用户名，null代表不使用该查询条件
     * @return 用户总数
     */
    int countByUsername(String fuzzyUsername);

    /**
     * 新增用户
     *
     * @param userToAdd 需要新增的用户数据，非空
     * @return 新增的行数
     */
    int add(UserController.UserToAdd userToAdd);

    /**
     * 更新用户
     *
     * @param userToUpdate 用户的最新数据，非空
     * @return 更新的行数
     */
    int update(UserController.UserToUpdate userToUpdate);

    /**
     * 更新密码
     *
     * @param userId   需要修改密码的用户ID，非空
     * @param password 编码后的密码，非空
     * @return 更新的行数
     */
    int updatePasswordById(int userId, String password);

    /**
     * 删除用户
     *
     * @param userId 需要删除的用户ID，非空
     * @return 删除的行数
     */
    int deleteById(int userId);
}
