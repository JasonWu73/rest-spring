package net.wuxianjie.springbootcore.security;

/**
 * Token 认证业务逻辑接口。
 *
 * @author 吴仙杰
 */
public interface TokenAuthenticationService {

    /**
     * 执行 Token 认证。
     */
    TokenUserDetails authenticate(String token);
}
