package net.wuxianjie.web.service;

import net.wuxianjie.core.domain.PaginationData;
import net.wuxianjie.web.domain.User;

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
}
