package net.wuxianjie.web.service;

import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.web.controller.UserController;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.model.Wrote2Database;

import java.util.List;

/**
 * 系统可登录用户的业务操作
 *
 * @author 吴仙杰
 */
public interface UserService {

  /**
   * 获取用户分页数据
   *
   * @param pageNo 页码，从0开始，非空
   * @param pageSize 每页显示条数，非空
   * @param fuzzyUsername 支持数据库模糊查询的用户名
   * @return 用户分页数据
   */
  PaginationData<List<User>> loadUsers(Integer pageNo, Integer pageSize, String fuzzyUsername);

  /**
   * 新增用户
   *
   * @param userToAdd 需要入库的用户数据
   * @return 新增操作的执行情况
   */
  Wrote2Database saveUser(UserController.UserToAdd userToAdd);

  /**
   * 修改用户
   *
   * @param userToUpdate 用户的最新数据
   * @return 更新操作的执行情况
   */
  Wrote2Database updateUser(UserController.UserToUpdate userToUpdate);

  /**
   * 修改密码
   *
   * @param passwordToUpdate 需要更新的密码
   * @return 修改密码操作的执行情况
   */
  Wrote2Database updatePassword(UserController.PasswordToUpdate passwordToUpdate);

  /**
   * 删除用户
   *
   * @param userId 需要删除的用户ID
   * @return 删除操作的执行情况
   */
  Wrote2Database removeUser(int userId);
}
