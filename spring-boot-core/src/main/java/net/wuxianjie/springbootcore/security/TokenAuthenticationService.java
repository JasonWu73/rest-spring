package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;

/**
 * Token 认证业务逻辑接口。
 *
 * @author 吴仙杰
 */
public interface TokenAuthenticationService {

    /**
     * 执行 Token 认证，返回认证通过后的用户详细数据。
     *
     * @param token 需要认证的 Token
     * @return 用户详细数据
     * @throws TokenAuthenticationException 当 Token 认证不通过时
     */
    TokenUserDetails authenticate(String token) throws TokenAuthenticationException;
}
