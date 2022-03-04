package net.wuxianjie.core.service;

import net.wuxianjie.core.dto.PrincipalDto;

/**
 * Token 认证机制的业务逻辑
 */
public interface TokenAuthenticationService {

    /**
     * 执行Token 认证
     *
     * @param accessToken Access Token
     * @return PrincipalDto
     */
    PrincipalDto authenticate(String accessToken);
}
