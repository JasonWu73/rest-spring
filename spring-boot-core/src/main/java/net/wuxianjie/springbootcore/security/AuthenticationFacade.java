package net.wuxianjie.springbootcore.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 可通过依赖注入方便地获取已通过认证后的 Token 详细数据。
 *
 * @author 吴仙杰
 */
@Service
public class AuthenticationFacade {

    /**
     * 获取已通过认证后的 Token 详细数据。若是开放接口，即无需 Token 认证的接口，则返回空。
     *
     * @return 认证后的 Token 详细数据。
     */
    public Optional<TokenDetails> getLoggedIn() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(authentication -> {
                    if (authentication instanceof AnonymousAuthenticationToken) {
                        // 匿名用户可访问的接口，则返回空
                        // authentication.getName() 为 anonymous
                        return null;
                    }

                    return (TokenDetails) authentication.getPrincipal();
                });
    }
}
