package net.wuxianjie.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.wuxianjie.core.model.CachedToken;
import org.springframework.security.core.Authentication;

/**
 * 身份认证工具类
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtils {

  /**
   * 获取当前通过身份认证的账号信息
   *
   * @param auth 可由Spring Controller方法自动注入参数获得
   * @return 程序内部的Token数据传输对象
   */
  public static CachedToken loadToken(@NonNull Authentication auth) {
    final Object principal = auth.getPrincipal();
    return (CachedToken) principal;
  }
}
