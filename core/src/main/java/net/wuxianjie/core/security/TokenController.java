package net.wuxianjie.core.security;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenController {

  private final TokenService tokenService;

  /**
   * 获取 Access Token。
   */
  @PostMapping("access_token")
  public TokenData getToken(@RequestBody @Validated GetTokenQuery param) {
    return tokenService.getToken(
      param.getAccountName(),
      param.getAccountPassword()
    );
  }

  /**
   * 刷新 Access Token。
   */
  @GetMapping("refresh_token/{refreshToken}")
  public TokenData refreshToken(@PathVariable String refreshToken) {
    return tokenService.refreshToken(refreshToken);
  }

  @Data
  private static class GetTokenQuery {

    @NotBlank(message = "账号名称不能为空")
    private String accountName;

    @NotBlank(message = "账号密码不能为空")
    private String accountPassword;
  }
}
