package net.wuxianjie.springbootcore.mybatis;

import org.apache.ibatis.annotations.*;

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

    @Insert("INSERT INTO users (create_time, username, birthday, is_enabled) " +
            "VALUES (#{createTime}, #{username}, #{birthday}, #{enabled})")
    int insertUser(User user);

    @Update("UPDATE users SET is_enabled = #{enabled} " +
            "WHERE username = #{username}")
    int updateUser(User user);

    @Delete("DELETE FROM users WHERE username =#{username}")
    int deleteUserByName(String newUsername);
}
