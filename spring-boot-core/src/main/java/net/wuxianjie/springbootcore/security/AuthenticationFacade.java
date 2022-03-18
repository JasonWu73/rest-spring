package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.InternalServerException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 封装经 Spring Security 认证后，获取用户详细数据的方法，使其可通过依赖注入方便地使用。
 *
 * @author 吴仙杰
 */
@Service
public class AuthenticationFacade {

    /**
     * 获取当前已登录的用户详细数据；若无法获取，则抛出 {@link InternalServerException}。
     */
    public TokenUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof AnonymousAuthenticationToken) {
            TokenUserDetails anonymous = new TokenUserDetails();
            anonymous.setAccountName(authentication.getName());
            return anonymous;
        }

        return (TokenUserDetails) Optional.ofNullable(authentication.getPrincipal())
                .orElseThrow(() -> new InternalServerException("无法获取已登录用户的详细数据"));
    }
}
