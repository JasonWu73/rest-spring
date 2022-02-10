package net.wuxianjie.core.service;

import net.wuxianjie.core.domain.Token;

/**
 * Token鉴权认证-Token续期策略
 *
 * <p><strong>被动刷新（推荐）</strong></p>
 *
 * <p>由客户端手动调用接口刷新Token完成续期</p>
 *
 * <p><strong>主动续期</strong></p>
 *
 * <p>由后端在Token验证通过且达到指定阈值时，自动续期（类似服务端session有效期策略）。适用于前后端分离的后台</p>
 *
 * @author 吴仙杰
 */
public interface TokenService {

  /**
   * 获取Access Token。若用户已存在Token，则返回该Token；否则返回一个新生成的Token
   *
   * @param accountName 账号名称
   * @param accountPassword 账号密码
   * @return Token
   */
  Token createToken(String accountName, String accountPassword);

  /**
   * 刷新Access Token。若刷新成功，则原Token将不可用
   *
   * @param refreshToken 用于刷新的Refresh Token
   * @return Token
   */
  Token updateToken(String refreshToken);
}
