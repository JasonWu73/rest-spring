package net.wuxianjie.web.role;

import lombok.Data;
import net.wuxianjie.web.security.RoleOfMenu;

/**
 * 修改角色请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class RequestOfUpdateRole {

  /**
   * 角色 id。
   */
  private Integer roleId;

  /**
   * 角色名。
   */
  private String roleName;

  /**
   * 角色绑定的菜单编号，多个菜单编号以英文逗号分隔，且仅需包含上级菜单编号即可。
   *
   * @see RoleOfMenu#value()
   */
  private String menus;
}
