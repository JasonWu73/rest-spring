package net.wuxianjie.core.service;

import lombok.NonNull;
import net.wuxianjie.core.model.dto.TokenDto;

/**
 * Token 鉴权认证 - Token 续期策略
 *
 * <p><strong>被动刷新 (推荐)</strong></p>
 *
 * <p>由客户端手动调用接口刷新 Token 完成续期</p>
 *
 * <p><strong>主动续期</strong></p>
 *
 * <p>由后端在 Token 验证通过且达到指定阈值时, 自动续期 (类似服务端 session 有效期策略).
 * 适用于前后端分离的后台</p>
 *
 * @author 吴仙杰
 */
public interface TokenService {

  /**
   * 获取 Access Token. 若用户已存在 Token, 则返回该 Token; 否则, 返回一个新生成的 Token
   *
   * @param accountName 账号名称
   * @param accountPassword 账号密码
   * @return Token
   */
  TokenDto createToken(@NonNull final String accountName, @NonNull final String accountPassword);

  /**
   * 刷新 Access Token. 若刷新成功, 则原 Token 将不可用
   *
   * @param refreshToken 用于刷新的 Refresh Token
   * @return Token
   */
  TokenDto updateToken(@NonNull final String refreshToken);
}
