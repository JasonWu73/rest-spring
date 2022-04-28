package net.wuxianjie.springbootcore.mybatis;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 吴仙杰
 */
@Mapper
interface UserMapper {

  @Select("SELECT is_enabled FROM users WHERE username = #{username}")
  YesOrNo selectEnabledByUsername(String username);

  @Select("SELECT is_enabled FROM users WHERE username = #{username}")
  Integer selectEnabledByUsernameReturnIntObj(String username);

  @Select("SELECT is_enabled FROM users WHERE username = #{username}")
  int selectEnabledByUsernameReturnInt(String username);

  @Select("SELECT -1")
  YesOrNo selectNegativeOne();

  @Select("SELECT create_time FROM users WHERE username = #{username}")
  LocalDateTime selectCreateTimeByUsername(String username);

  @Select("SELECT create_time FROM users WHERE username = #{username}")
  String selectCreateTimeStrByUsername(String username);

  @Select("SELECT birthday FROM users WHERE username = #{username}")
  String selectBirthdayStrByUsername(String username);

  @Select("SELECT birthday FROM users WHERE username = #{username}")
  LocalDate selectBirthdayByUsername(String username);

  @Insert(
    "INSERT INTO users (create_time, username, birthday, is_enabled) " +
      "VALUES (#{createTime}, #{username}, #{birthday}, #{enabled})"
  )
  void insertUser(User user);
}
