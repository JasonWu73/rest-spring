package net.wuxianjie.springbootcore.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * Access Token API 控制器。
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
  public TokenData getToken(@RequestBody @Valid Query query) throws TokenAuthenticationException {
    return tokenService.getToken(query.getUsername(), query.getPassword());
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
     * 用户名。
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码。
     */
    @NotBlank(message = "密码不能为空")
    private String password;
  }
}
