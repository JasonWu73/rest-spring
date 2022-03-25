package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;

/**
 * Token 认证业务逻辑接口。
 *
 * @author 吴仙杰
 */
public interface TokenAuthenticationService {

    /**
     * 执行 Token 认证，返回认证通过后的 Token 详细数据。
     *
     * @param token 需要认证的 Token
     * @return Token 详细数据
     * @throws TokenAuthenticationException 若 Token 认证不通过
     */
    TokenDetails authenticate(String token)
            throws TokenAuthenticationException;
}
