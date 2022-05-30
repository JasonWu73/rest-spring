package net.wuxianjie.web.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单项的 API 控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

  /**
   * 获取全部菜单项。
   *
   * @return 树形结构的全部菜单项数据
   */
  @GetMapping("list")
  public MenuItem getAllMenus() {
    return RoleOfMenu.getAllMenus();
  }
}
