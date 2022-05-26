package net.wuxianjie.web.role;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.web.oplog.OpLogger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 角色管理的 API 控制器。
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
   * @return 全部角色列表
   */
  @GetMapping("list")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_ROLE_LIST.name())")
  public List<Role> getAllRoles() {
    return roleService.getAllRoles();
  }

  /**
   * 新增角色。
   *
   * @param query 需要保存的角色数据
   * @return 新增成功后的提示信息
   */
  @PostMapping("add")
  @OpLogger("新增角色")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_ROLE_ADD.name())")
  public Map<String, String> saveRole(@RequestBody @Valid SaveRoleQuery query) {
    return roleService.saveRole(query);
  }

  /**
   * 修改角色。
   *
   * @param roleId 角色 id
   * @param query  需要更新的角色数据
   * @return 修改成功后的提示信息
   */
  @PostMapping("update/{roleId:\\d+}")
  @OpLogger("修改角色")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_ROLE_UPDATE.name())")
  public Map<String, String> updateRole(@PathVariable int roleId,
                         @RequestBody @Valid UpdateRoleQuery query) {
    query.setRoleId(roleId);
    return roleService.updateRole(query);
  }

  /**
   * 删除角色。
   *
   * @param roleId 需要删除的角色 id
   * @return 删除成功后的提示信息
   */
  @GetMapping("del/{roleId:\\d+}")
  @OpLogger("删除角色")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_ROLE_DEL.name())")
  public Map<String, String> deleteRole(@PathVariable int roleId) {
    return roleService.deleteRole(roleId);
  }
}
