package net.wuxianjie.web.role;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 与角色表相关的 SQL。
 *
 * @author 吴仙杰
 */
@Mapper
public interface RoleMapper {

  /**
   * 保存角色数据。
   *
   * @param role 需要保存的角色数据
   */
  void save(Role role);

  /**
   * 通过角色 id 获取角色数据。
   *
   * @param roleId 角色 id
   * @return 角色数据
   */
  Role findByRoleId(int roleId);

  /**
   * 获取全部角色列表。
   *
   * @return 全部角色列表
   */
  List<Role> findAll();

  /**
   * 更新角色数据。
   *
   * @param role 需要更新的角色数据
   */
  void update(Role role);

  /**
   * 删除角色。
   *
   * @param roleId 需要删除的角色 id
   */
  void deleteByRoleId(int roleId);
}
