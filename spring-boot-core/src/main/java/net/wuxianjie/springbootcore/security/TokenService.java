package net.wuxianjie.springbootcore.security;

/**
 * Access Token 管理。
 *
 * @author 吴仙杰
 */
public interface TokenService {

    /**
     * 获取 Access Token。
     */
    TokenData getToken(String accountName, String accountRawPassword);

    /**
     * 刷新 Access Token。
     */
    TokenData refreshToken(String refreshToken);
}
