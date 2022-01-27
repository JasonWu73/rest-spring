package net.wuxianjie.core.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.model.dto.TokenDto;
import net.wuxianjie.core.service.TokenService;
import net.wuxianjie.core.constant.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 符合 Token 鉴权认证机制的 Access Token 获取与刷新控制器
 *
 * @author 吴仙杰
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenController {

  private final TokenService tokenService;

/**
   * 获取 Access Token. 若用户已存在 Token, 则返回该 Token; 否则, 返回一个新生成的 Token
   *
   * @param query 请求参数
   * @return Token
   */
  @PostMapping(Mappings.ACCESS_TOKEN)
  public TokenDto createToken(@RequestBody @Valid createTokenQuery query) {
    return tokenService.createToken(query.getAccountName(), query.getAccountPassword());
  }

  /**
   * 刷新 Access Token. 若刷新成功, 则原 Token 将不可用
   *
   * @param token 用于刷新的 Refresh Token
   * @return Token
   */
  @GetMapping(Mappings.REFRESH_TOKEN)
  public TokenDto updateToken(
      @NotBlank(message = "Refresh Token 不能为空") String token) {
    return tokenService.updateToken(token);
  }

  @Data
  private static class createTokenQuery {

    /** 账号名称, 非空 */
    @NotBlank(message = "账号名称不能为空")
    private String accountName;

    /** 账号密码, 非空 */
    @NotBlank(message = "账号密码不能为空")
    private String accountPassword;
  }
}
