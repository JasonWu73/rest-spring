package net.wuxianjie.core.service;

import net.wuxianjie.core.dto.PrincipalDto;

/**
 * 封装获取 Spring Security 登录账号信息的工具 Bean
 */
public interface AuthenticationFacade {

    /**
     * 获取身份认证后的账号主体信息
     *
     * @return PrincipalDto
     */
    PrincipalDto getCacheToken();
}
