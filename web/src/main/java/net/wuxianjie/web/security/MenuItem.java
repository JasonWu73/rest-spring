package net.wuxianjie.web.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 菜单项数据。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

  /**
   * 菜单名称。
   *
   * @see SysMenu#msg()
   */
  private String name;

  /**
   * 菜单编号。
   *
   * @see SysMenu#value()
   */
  private String code;

  /**
   * 子菜单项。
   */
  private List<MenuItem> children;
}
