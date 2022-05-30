package net.wuxianjie.web.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

/**
 * 角色层级配置类。
 *
 * @author 吴仙杰
 */
@Configuration
public class RoleHierarchyConfig {

  /**
   * 配置角色层级结构。
   *
   * @return 角色层级对象
   */
  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(RoleOfMenu.getRoleHierarchyStr());
    return roleHierarchy;
  }
}
