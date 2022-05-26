package net.wuxianjie.web.role;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import net.wuxianjie.springbootcore.util.StrUtils;
import net.wuxianjie.web.user.ComUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 角色管理的业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleMapper roleMapper;
  private final ComUserService comUserService;

  /**
   * 获取全部角色列表。
   *
   * @return 全部角色列表
   */
  public List<Role> getAllRoles() {
    return roleMapper.findAll();
  }

  /**
   * 新增角色。
   *
   * @param query 需要保存的角色数据
   * @return 新增成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> saveRole(SaveRoleQuery query) {
    // 校验并去重菜单编号字符串
    comUserService.toDeduplicatedCommaSeparatedMenus(query.getMenus()).ifPresent(query::setMenus);

    // 角色入库
    Role toSave = new Role();
    String roleName = query.getRoleName();
    toSave.setRoleName(roleName);
    toSave.setMenus(query.getMenus());
    roleMapper.save(toSave);

    return new HashMap<>() {{
      put("msg", "角色（" + roleName + "）新增成功");
    }};
  }

  /**
   * 修改角色。
   *
   * @param query 需要更新的角色数据
   * @return 修改成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> updateRole(UpdateRoleQuery query) {
    // 校验并去重菜单编号字符串
    comUserService.toDeduplicatedCommaSeparatedMenus(query.getMenus()).ifPresent(query::setMenus);

    // 判断角色是否存在
    Role oldRole = getRoleFromDbMustBeExists(query.getRoleId());
    String roleName = oldRole.getRoleName();

    // 判断是否需要更新
    Optional<Role> toUpdate = getRoleToUpdate(oldRole, query);
    if (toUpdate.isEmpty()) return new HashMap<>() {{
      put("msg", "角色（" + roleName + "）无需修改");
    }};

    // 更新数据
    roleMapper.update(toUpdate.get());

    return new HashMap<>() {{
      put("msg", "角色（" + roleName + "）修改成功");
    }};
  }

  /**
   * 删除角色。
   *
   * @param roleId 需要删除的角色 id
   * @return 删除成功后的提示信息
   */
  @Transactional(rollbackFor = Exception.class)
  public Map<String, String> deleteRole(int roleId) {
    // 判断角色是否存在
    Role toDel = getRoleFromDbMustBeExists(roleId);
    String roleName = toDel.getRoleName();

    // 删除数据
    roleMapper.deleteByRoleId(roleId);

    return new HashMap<>() {{
      put("msg", "角色（" + roleName + "）删除成功");
    }};
  }

  private Role getRoleFromDbMustBeExists(int roleId) {
    return Optional.ofNullable(roleMapper.findByRoleId(roleId))
      .orElseThrow(() -> new NotFoundException("未找到 id 为 " + roleId + " 的角色"));
  }

  private Optional<Role> getRoleToUpdate(Role oldRole, UpdateRoleQuery query) {
    boolean needsUpdate = false;
    Role toUpdate = new Role();
    toUpdate.setRoleId(oldRole.getRoleId());

    String newRoleName = query.getRoleName();
    if (equalsField(newRoleName, oldRole.getRoleName())) {
      needsUpdate = true;
      toUpdate.setRoleName(newRoleName);
    }

    String newMenus = query.getMenus();
    if (equalsField(newMenus, oldRole.getMenus())) {
      needsUpdate = true;
      toUpdate.setMenus(newMenus);
    }

    if (!needsUpdate) return Optional.empty();

    return Optional.of(toUpdate);
  }

  private boolean equalsField(String newValue, String oldValue) {
    return newValue != null && !StrUtils.equalsIgnoreBlank(newValue, oldValue);
  }
}
