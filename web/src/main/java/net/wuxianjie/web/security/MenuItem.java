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
   * 菜单 id。
   */
  private Integer id;

  /**
   * 菜单名称。
   */
  private String name;

  /**
   * 菜单编号。
   */
  private String code;

  /**
   * 上级菜单的菜单 id。
   */
  private Integer pid;

  private List<MenuItem> children;
}
