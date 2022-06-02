package net.wuxianjie.web.security;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude
public class MenuItem {

  /**
   * 菜单名称。
   *
   * @see RoleOfMenu#msg()
   */
  private String name;

  /**
   * 菜单编号。
   *
   * @see RoleOfMenu#value()
   */
  private String code;

  /**
   * 当前用户是否拥有该菜单项。
   */
  private Boolean has;

  /**
   * 子菜单项。
   */
  private List<MenuItem> children;

  public MenuItem(String name, String code, List<MenuItem> children) {
    this.name = name;
    this.code = code;
    this.children = children;
  }
}
