package net.wuxianjie.core.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.model.Token;
import net.wuxianjie.core.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 符合Token鉴权认证机制的Access Token获取与刷新控制器
 *
 * @author 吴仙杰
 */
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenController {

  private final TokenService tokenService;

/**
   * 获取Access Token。若用户已存在Token，则返回该Token；否则返回一个新生成的Token
   *
   * @param query 请求参数
   * @return Token
   */
  @PostMapping("/access_token")
  public Token createToken(@Valid @RequestBody final Query query) {
    return tokenService.createToken(query.getAccountName(), query.getAccountPassword());
  }

  /**
   * 刷新Access Token。若刷新成功，则原Token将不可用
   *
   * @param refreshToken 用于刷新的Refresh Token
   * @return Token
   */
  @GetMapping("/refresh_token/{refreshToken}")
  public Token updateToken(
      @NotBlank(message = "Refresh Token不能为空")
      @PathVariable
      final String refreshToken) {
    return tokenService.updateToken(refreshToken);
  }

  @Data
  private static class Query {

    /** 账号名称，必填 */
    @NotBlank(message = "账号不能为空")
    private String accountName;

    /** 账号密码，必填 */
    @NotBlank(message = "密码不能为空")
    private String accountPassword;
  }
}
