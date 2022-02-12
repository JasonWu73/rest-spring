package net.wuxianjie.web.mapper;

import net.wuxianjie.web.controller.UserController;
import net.wuxianjie.web.model.Account;
import net.wuxianjie.web.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户数据的SQL映射器
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/mybatis">Quick Guide to MyBatis | Baeldung</a>
 */
@Mapper
public interface UserMapper {

  /**
   * 根据用户ID获取特定账户数据（包含密码的用户数据）
   *
   * @param userId 用户ID
   * @return 特定账户数据（包含密码的用户数据）
   */
  Account findAccountByUserId(int userId);

  /**
   * 根据用户名查询用户数据
   *
   * @param from 从哪条数据开始（即{@code pageNo x pageSize}）
   * @param pageSize 每页条数
   * @param username 支持数据库模糊查询的用户名
   * @return 用户分页数据
   */
  List<User> findUsersPaginationByUsername(Integer from, Integer pageSize, String username);

  /**
   * 根据用户名查询用户总记录数
   *
   * @param username 支持数据库模糊查询的用户名
   * @return 过滤后的总记录数
   */
  int findUserCountByUsername(String username);

  /**
   * 新增用户
   *
   * @param userToAdd 需要入库的用户数据
   * @return 新增的行数
   */
  int addUser(UserController.UserToAdd userToAdd);

  /**
   * 更新用户
   *
   * @param userToUpdate 用户的最新数据
   * @return 更新的行数
   */
  int updateUser(UserController.UserToUpdate userToUpdate);

  /**
   * 删除用户
   *
   * @param userId 需要删除的用户ID
   * @return 删除的行数
   */
  int deleteUserByUserId(int userId);
}
