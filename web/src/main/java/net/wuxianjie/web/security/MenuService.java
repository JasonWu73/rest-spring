package net.wuxianjie.web.security;

import net.wuxianjie.springbootcore.security.AuthenticationUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 菜单业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Service
public class MenuService {

  /**
   * 获取当前用户的菜单项
   *
   * @return 树形结构的全部菜单项数据
   */
  public List<MenuItem> getCurrentUserMenus() {
    // 获取全部菜单项
    MenuItem rootMenu = RoleOfMenu.getAllMenus();

    // 过滤并只返回当前用户所绑定的菜单
    // 因为硬编码最多三级菜单，故这里遍历三层即可
    Optional<TokenUserDetails> user = AuthenticationUtils.getCurrentUser();
    String roles = user.orElseThrow().getRoles();
    List<String> codes = new ArrayList<>(Arrays.asList(roles.split(",")));
    if (codes.contains(rootMenu.getCode())) return List.of(rootMenu);

    // 一级
    List<MenuItem> menuList = new ArrayList<>();
    List<MenuItem> children = rootMenu.getChildren();
    for (MenuItem first : children) {
      if (codes.contains(first.getCode())) {
        menuList.add(first);
        continue;
      }

      // 二级
      for (MenuItem second : first.getChildren()) {
        if (codes.contains(second.getCode())) menuList.add(second);

        // 三级
        if (second.getChildren() == null) continue;

        for (MenuItem third : second.getChildren()) {
          if (codes.contains(third.getCode())) menuList.add(third);
        }
      }
    }
    return menuList;
  }
}
