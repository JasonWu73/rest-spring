package net.wuxianjie.web.controller;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.annotation.User;
import net.wuxianjie.core.annotation.UserOrAdmin;
import net.wuxianjie.core.constant.AuthRole;
import net.wuxianjie.core.service.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 关于对Token鉴权认证机制进行测试的REST API控制器
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/auth-resource")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthResourceController {

  private final AuthenticationFacade authentication;

  /**
   * 游客（匿名用户）可访问，即无需身份认证即可访问
   */
  @GetMapping("public")
  public String getAnonymousResource() {
    return String.format("您好：%s！您正在访问无需身份认证即可访问的资源",
        authentication.getCacheToken().getAccountName());
  }

  /**
   * 来宾（角色为空的用户），即只要通过身份认证即可访问
   */
  @GetMapping("guest")
  public String getGuestResource() {
    return String.format("您好：%s，您正在访问只要通过身份认证即可访问的资源",
        authentication.getCacheToken().getAccountName());
  }

  /**
   * 通过身份认证，且必须拥有普通用户角色才能访问
   */
  @User
  @GetMapping("user")
  public String getUserResource() {
    return String.format("您好：%s，您正在访问必须拥有【%s】角色才可访问的资源",
        authentication.getCacheToken().getAccountName(), AuthRole.USER.value());
  }

  /**
   * 通过身份认证，且必须拥有管理员角色才能访问
   */
  @Admin
  @GetMapping("admin")
  public String getAdminResource() {
    return String.format("您好：%s，您正在访问必须拥有【%s】角色才可访问的资源",
        authentication.getCacheToken().getAccountName(), AuthRole.ADMIN.value());
  }

  /**
   * 通过身份认证，且必须拥有普通用户或管理员角色才能访问
   */
  @UserOrAdmin
  @GetMapping("user-or-admin")
  public String getUserOrAdmin() {
    return String.format("您好：%s，您正在访问只要拥有【%s或%s】一种角色才可访问的资源",
        authentication.getCacheToken().getAccountName(),
        AuthRole.USER.value(), AuthRole.ADMIN.value());
  }
}
