package net.wuxianjie.web.controller;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.model.dto.CachedTokenDto;
import net.wuxianjie.core.util.AuthUtils;
import net.wuxianjie.web.constant.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Token 鉴权认证机制下的资源访问测试控制器
 *
 * @author 吴仙杰
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthResourceController {

  /**
   * 匿名用户
   */
  @GetMapping(Mappings.ANONYMOUS)
  public String loadAnonymous() {
    return "任何人都可访问的资源";
  }

  /**
   * 来宾用户 (角色为空的用户), 即任何通过身份认证的用户都可访问的资源
   *
   * @param auth 通过身份认证后的本地账号信息
   */
  @GetMapping(Mappings.GUEST)
  public String loadGuest(Authentication auth) {
    final CachedTokenDto tokenDto = AuthUtils.loadToken(auth);
    return String.format("您好 %s, 您正在访问只要通过身份认证后即可访问的资源", tokenDto.getAccountName());
  }

  /**
   * 普通用户
   *
   * @param auth 通过身份认证后的本地账号信息
   */
  @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
  @GetMapping(Mappings.USER)
  public String loadUser(Authentication auth) {
    final CachedTokenDto tokenDto = AuthUtils.loadToken(auth);
    return String.format("您好 %s, 您正在访问 USER 角色才可访问的资源", tokenDto.getAccountName());
  }

  /**
   * 管理员
   *
   * @param auth 通过身份认证后的本地账号信息
   */
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(Mappings.ADMIN)
  public String loadAdmin(Authentication auth) {
    final CachedTokenDto tokenDto = AuthUtils.loadToken(auth);
    return String.format("您好 %s, 您正在访问 ADMIN 角色才可访问的资源", tokenDto.getAccountName());
  }
}
