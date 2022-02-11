package net.wuxianjie.web.controller;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.annotation.User;
import net.wuxianjie.core.annotation.UserOrAdmin;
import net.wuxianjie.core.constant.AuthRole;
import net.wuxianjie.core.model.CachedToken;
import net.wuxianjie.core.util.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token鉴权认证机制下的资源访问测试控制器
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/auth-resource")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthResourceController {

  /**
   * 游客、匿名用户，即不用身份认证就可以访问
   */
  @GetMapping("public")
  public String loadAnonymous() {
    return "您好！您正在访问任何人都可访问的资源";
  }

  /**
   * 来宾（角色为空的用户），即任何通过身份认证的用户都可访问
   *
   * @param auth 通过身份认证后的本地账号信息
   */
  @GetMapping("guest")
  public String loadGuest(Authentication auth) {
    final CachedToken tokenDto = AuthUtils.loadToken(auth);
    return String.format("您好：%s，您正在访问只要通过身份认证后即可访问的资源", tokenDto.getAccountName());
  }

  /**
   * 仅普通用户可以访问
   *
   * @param auth 通过身份认证后的本地账号信息
   */
  @User
  @GetMapping("user")
  public String loadUser(Authentication auth) {
    final CachedToken tokenDto = AuthUtils.loadToken(auth);
    return String.format("您好：%s，您正在访问需要拥有【%s】角色才可访问的资源",
        tokenDto.getAccountName(), AuthRole.USER.value());
  }

  /**
   * 仅管理员可访问
   *
   * @param auth 通过身份认证后的本地账号信息
   */
  @Admin
  @GetMapping("admin")
  public String loadAdmin(Authentication auth) {
    final CachedToken tokenDto = AuthUtils.loadToken(auth);
    return String.format("您好：%s，您正在访问拥有【%s】角色才可访问的资源",
        tokenDto.getAccountName(), AuthRole.ADMIN.value());
  }

  /**
   * 普通用户或管理员都可访问
   *
   * @param auth 通过身份认证后的本地账号信息
   */
  @UserOrAdmin
  @GetMapping("user-or-admin")
  public String loadUserOrAdmin(Authentication auth) {
    final CachedToken tokenDto = AuthUtils.loadToken(auth);
    return String.format("您好：%s，您正在访问拥有【%s或%s】角色才可访问的资源",
        tokenDto.getAccountName(), AuthRole.USER.value(), AuthRole.ADMIN.value());
  }
}
