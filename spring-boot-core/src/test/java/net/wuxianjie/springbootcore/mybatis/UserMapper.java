package net.wuxianjie.springbootcore.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 吴仙杰
 */
@Mapper
interface UserMapper {

    @Select("SELECT user_id AS userId, " +
            "create_time AS createTime, " +
            "username, " +
            "birthday, " +
            "is_enabled AS enabled " +
            "FROM users")
    List<User> selectAllUsers();
}
