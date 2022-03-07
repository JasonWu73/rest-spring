package net.wuxianjie.core.rest.auth;

import net.wuxianjie.core.rest.auth.dto.PrincipalDto;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 获取 Spring Security 登录账号信息的工具 Bean
 */
@Service
public class AuthenticationFacade {

    /**
     * 获取身份认证后的账号主体信息
     *
     * @return PrincipalDto
     */
    public PrincipalDto getPrincipal() {
        final Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            final PrincipalDto anonymous = new PrincipalDto();
            anonymous.setAccountName(authentication.getName());
            return anonymous;
        }

        return (PrincipalDto) authentication.getPrincipal();
    }
}
