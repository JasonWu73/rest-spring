package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;

/**
 * Access Token 管理业务逻辑接口。
 *
 * @author 吴仙杰
 */
public interface TokenService {

    /**
     * 获取 Access Token。
     *
     * @param accountName 账号名
     * @param rawPassword 明文密码
     * @return {@link TokenData}
     * @throws TokenAuthenticationException 若因账号原因而导致无法获取 Token
     */
    TokenData getToken(String accountName, String rawPassword) throws TokenAuthenticationException;

    /**
     * 刷新 Access Token。
     *
     * @param refreshToken 用于刷新的 Token
     * @return {@link TokenData}
     * @throws TokenAuthenticationException 若因账号原因而导致无法获取 Token
     */
    TokenData refreshToken(String refreshToken) throws TokenAuthenticationException;
}
