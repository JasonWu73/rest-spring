package net.wuxianjie.web.role;

import lombok.Data;
import net.wuxianjie.web.security.SysMenu;

import javax.validation.constraints.NotBlank;

/**
 * 新增角色请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class RequestOfSaveRole {

  /**
   * 角色名。
   */
  @NotBlank(message = "角色名不能为空")
  private String roleName;

  /**
   * 角色绑定的菜单编号，多个菜单编号以英文逗号分隔，且仅需包含上级菜单编号即可。
   *
   * @see SysMenu#value()
   */
  @NotBlank(message = "菜单编号不能为空")
  private String menus;
}
