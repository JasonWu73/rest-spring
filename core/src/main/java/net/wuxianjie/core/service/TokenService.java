package net.wuxianjie.core.service;

import net.wuxianjie.core.dto.TokenDto;

/**
 * Token 续期策略
 *
 * <p></p>
 * <p><h2>被动刷新（推荐）</h2></p>
 *
 * <p>由客户端手动调用接口刷新 Token 完成续期</p>
 *
 * <p></p>
 * <p><h2>主动续期</h2></p>
 *
 * <p>由后端在 Token 验证通过且达到指定阈值时，
 * 自动续期（类似服务端 session 有效期策略）。
 * 适用于前后端分离的后台</p>
 */
public interface TokenService {

    /**
     * 获取 Access Token
     *
     * @param accountName 账号名称
     * @param rawPassword 账号密码
     * @return Token
     */
    TokenDto getToken(String accountName, String rawPassword);

    /**
     * 刷新 Access Token
     *
     * @param refreshToken 用于刷新的 Refresh Token
     * @return Token
     */
    TokenDto refreshToken(String refreshToken);
}
