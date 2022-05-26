package net.wuxianjie.web.user;

import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.validator.EnumValidator;
import net.wuxianjie.web.security.SysMenu;

/**
 * 修改用户的请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class UpdateUserQuery {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 启用状态：1：启用，0：禁用。
   */
  @EnumValidator(message = "启用状态不合法", value = YesOrNo.class)
  private Integer enabled;

  /**
   * 用户绑定的菜单编号，多个菜单编号以英文逗号分隔，且仅需包含上级菜单编号即可。
   *
   * @see SysMenu#value()
   */
  private String menus;
}
