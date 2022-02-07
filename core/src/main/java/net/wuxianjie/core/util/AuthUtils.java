package net.wuxianjie.core.util;

import lombok.NonNull;
import net.wuxianjie.core.domain.CachedToken;
import org.springframework.security.core.Authentication;

/**
 * 身份认证工具类
 *
 * @author 吴仙杰
 */
public class AuthUtils {

  /**
   * 获取当前通过身份认证的账号信息
   *
   * @param auth 可由 Spring Controller 方法自动注入参数获得
   * @return 程序内部的 Token 数据传输对象
   */
  public static CachedToken loadToken(@NonNull Authentication auth) {
    final Object principal = auth.getPrincipal();
    return (CachedToken) principal;
  }
}
