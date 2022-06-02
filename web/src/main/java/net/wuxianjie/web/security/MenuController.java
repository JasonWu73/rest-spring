package net.wuxianjie.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单项的 API 控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService menuService;

  /**
   * 获取当前用户的菜单项。
   *
   * @return 树形结构的全部菜单项数据
   */
  @GetMapping("list")
  public List<MenuItem> getCurrentUserMenus() {
    return menuService.getCurrentUserMenus();
  }
}
