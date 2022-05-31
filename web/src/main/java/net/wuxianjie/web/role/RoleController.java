package net.wuxianjie.web.role;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.web.operationlog.OperationLogger;
import net.wuxianjie.web.shared.SimpleResultOfWriteOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色 API 控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

  private final RoleService roleService;

  /**
   * 获取全部角色列表。
   *
   * @return 角色列表
   */
  @GetMapping("list")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_ROLE.name())")
  public List<Role> getAllRoles() {
    return roleService.getAllRoles();
  }

  /**
   * 新增角色。
   *
   * @param query 需要保存的角色数据
   * @return 操作结果
   */
  @PostMapping("add")
  @OperationLogger("新增角色")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_ROLE_ADD.name())")
  public SimpleResultOfWriteOperation saveRole(@RequestBody @Valid RequestOfSaveRole query) {
    return roleService.saveRole(query);
  }

  /**
   * 修改角色。
   *
   * @param roleId 角色 id
   * @param query  需要更新的角色数据
   * @return 操作结果
   */
  @PostMapping("update/{roleId:\\d+}")
  @OperationLogger("修改角色")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_ROLE_UPDATE.name())")
  public SimpleResultOfWriteOperation updateRole(@PathVariable int roleId,
                                                 @RequestBody @Valid RequestOfUpdateRole query) {
    query.setRoleId(roleId);
    return roleService.updateRole(query);
  }

  /**
   * 删除角色。
   *
   * @param roleId 需要删除的角色 id
   * @return 操作结果
   */
  @GetMapping("del/{roleId:\\d+}")
  @OperationLogger("删除角色")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_ROLE_DEL.name())")
  public SimpleResultOfWriteOperation deleteRole(@PathVariable int roleId) {
    return roleService.deleteRole(roleId);
  }
}
