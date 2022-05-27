package net.wuxianjie.web.role;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.ConflictException;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import net.wuxianjie.springbootcore.util.StringUtils;
import net.wuxianjie.web.shared.SimpleResultOfWriteOperation;
import net.wuxianjie.web.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 角色业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleMapper roleMapper;
  private final UserService userService;

  /**
   * 获取全部角色列表。
   *
   * @return 角色列表
   */
  public List<Role> getAllRoles() {
    return roleMapper.findAll();
  }

  /**
   * 新增角色。
   *
   * @param query 需要保存的角色数据
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation saveRole(RequestOfSaveRole query) {
    // 校验并去重菜单编号字符串
    userService.toDeduplicatedCommaSeparatedMenus(query.getMenus())
      .ifPresent(query::setMenus);

    // 角色名唯一性校验
    String roleName = query.getRoleName();
    boolean isExisted = roleMapper.existsByRoleName(roleName);
    if (isExisted) throw new ConflictException(StrUtil.format("已存在相同角色名 [{}]", roleName));

    // 保存角色数据
    Role roleToSave = new Role();
    roleToSave.setRoleName(roleName);
    roleToSave.setMenus(query.getMenus());
    roleMapper.save(roleToSave);

    return new SimpleResultOfWriteOperation(StrUtil.format("新增角色 [{}]", roleName));
  }

  /**
   * 修改角色。
   *
   * @param query 需要更新的角色数据
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation updateRole(RequestOfUpdateRole query) {
    // 校验并去重菜单编号字符串
    userService.toDeduplicatedCommaSeparatedMenus(query.getMenus())
      .ifPresent(query::setMenus);

    // 检查角色是否存在
    Role oldRole = getRoleFromDatabaseMustBeExists(query.getRoleId());
    String roleName = oldRole.getRoleName();

    // 检查是否需要更新
    Optional<Role> roleToUpdate = getRoleToUpdate(oldRole, query);
    if (roleToUpdate.isEmpty()) return new SimpleResultOfWriteOperation("无需修改");

    // 更新角色数据
    roleMapper.update(roleToUpdate.get());

    return new SimpleResultOfWriteOperation(StrUtil.format("修改角色 [{}]", roleName));
  }

  /**
   * 删除角色。
   *
   * @param roleId 需要删除的角色 id
   * @return 操作结果
   */
  @Transactional(rollbackFor = Exception.class)
  public SimpleResultOfWriteOperation deleteRole(int roleId) {
    // 检查角色是否存在
    Role roleToDel = getRoleFromDatabaseMustBeExists(roleId);
    String roleName = roleToDel.getRoleName();

    // 删除角色
    roleMapper.deleteByRoleId(roleId);

    return new SimpleResultOfWriteOperation(StrUtil.format("删除角色 [{}]", roleName));
  }

  private Role getRoleFromDatabaseMustBeExists(int roleId) {
    return Optional.ofNullable(roleMapper.findByRoleId(roleId))
      .orElseThrow(() -> new NotFoundException(StrUtil.format("未找到角色 [roleId={}]", roleId)));
  }

  private Optional<Role> getRoleToUpdate(Role oldRole, RequestOfUpdateRole query) {
    boolean needsUpdate = false;

    Role roleToUpdate = new Role();
    roleToUpdate.setRoleId(oldRole.getRoleId());

    String newRoleName = query.getRoleName();
    if (equalsFieldValue(newRoleName, oldRole.getRoleName())) {
      needsUpdate = true;
      roleToUpdate.setRoleName(newRoleName);
    }

    String newMenus = query.getMenus();
    if (equalsFieldValue(newMenus, oldRole.getMenus())) {
      needsUpdate = true;
      roleToUpdate.setMenus(newMenus);
    }

    if (!needsUpdate) return Optional.empty();

    return Optional.of(roleToUpdate);
  }

  private boolean equalsFieldValue(String newValue, String oldValue) {
    return newValue != null && !StringUtils.equalsIgnoreBlank(newValue, oldValue);
  }
}
