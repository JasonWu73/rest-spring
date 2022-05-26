package net.wuxianjie.springbootcore.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

/**
 * Access Token 管理控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequiredArgsConstructor
public class TokenController {

  private final TokenService tokenService;

  /**
   * 获取 Access Token。
   *
   * @param query 请求参数
   * @return {@link TokenData}
   * @throws TokenAuthenticationException 当 Token 验证失败时抛出
   */
  @PostMapping(WebSecurityConfig.ACCESS_TOKEN_PATH)
  public TokenData getToken(@RequestBody @Validated Query query) throws TokenAuthenticationException {
    return tokenService.getToken(query.getAccount(), query.getPassword());
  }

  /**
   * 刷新 Access Token。
   *
   * @param refreshToken 用于刷新的 Token
   * @return {@link TokenData}
   * @throws TokenAuthenticationException 当 Token 验证失败时抛出
   */
  @GetMapping(WebSecurityConfig.REFRESH_TOKEN_PATH_PREFIX + "/{refreshToken:.+}")
  public TokenData refreshToken(@PathVariable String refreshToken) throws TokenAuthenticationException {
    return tokenService.refreshToken(refreshToken);
  }

  @Data
  private static class Query {

    /**
     * 账号。
     */
    @NotBlank(message = "账号不能为空")
    private String account;

    /**
     * 密码。
     */
    @NotBlank(message = "密码不能为空")
    private String password;
  }
}
