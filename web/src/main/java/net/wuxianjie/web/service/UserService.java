package net.wuxianjie.web.service;

import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.model.PaginationQuery;
import net.wuxianjie.web.controller.UserController;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.model.Wrote2Database;

import java.util.List;

/**
 * 用户管理业务逻辑接口
 *
 * @author 吴仙杰
 */
public interface UserService {

  /**
   * 根据分页条件及用户名获取用户列表数据
   *
   * @param pagination 分页条件，非空
   * @param fuzzyUsername 支持数据库模糊查询的用户名，null代表不使用该查询条件
   * @return 用户列表分页数据
   */
  PaginationData<List<User>> getUsers(PaginationQuery pagination, String fuzzyUsername);

  /**
   * 新增用户
   *
   * @param userToAdd 需要新增的用户数据，非空
   * @return 数据库的写入情况及说明
   */
  Wrote2Database saveUser(UserController.UserToAdd userToAdd);

  /**
   * 修改用户
   *
   * @param userToUpdate 用户的最新数据，非空
   * @return 数据库的写入情况及说明
   */
  Wrote2Database updateUser(UserController.UserToUpdate userToUpdate);

  /**
   * 修改密码
   *
   * @param passwordToUpdate 需要更新的密码，非空
   * @return 数据库的写入情况及说明
   */
  Wrote2Database updatePassword(UserController.PasswordToUpdate passwordToUpdate);

  /**
   * 删除用户
   *
   * @param userId 需要删除的用户ID，非空
   * @return 数据库的写入情况及说明
   */
  Wrote2Database removeUser(int userId);
}
