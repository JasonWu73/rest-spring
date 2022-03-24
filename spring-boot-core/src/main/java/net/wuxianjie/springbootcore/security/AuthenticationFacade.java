package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.InternalServerException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 可通过依赖注入方便地获取 Token 认证后的用户详细数据。
 *
 * @author 吴仙杰
 */
@Service
public class AuthenticationFacade {

    /**
     * 获取 Token 认证后的用户详细数据。
     *
     * @return 用户详细数据
     * @throws InternalServerException 若无法获取用户详细数据
     */
    public TokenUserDetails getCurrentUser() throws InternalServerException {
        return Optional.ofNullable(
                        SecurityContextHolder.getContext().getAuthentication()
                )
                .map(authentication -> {
                            if (authentication
                                    instanceof AnonymousAuthenticationToken
                            ) {
                                TokenUserDetails anonymous =
                                        new TokenUserDetails();

                                anonymous.setAccountName(
                                        authentication.getName()
                                );

                                return anonymous;
                            }

                            return Optional.ofNullable(
                                            (TokenUserDetails)
                                                    (authentication
                                                            .getPrincipal()
                                                    )
                                    )
                                    .orElseThrow(() ->
                                            new InternalServerException(
                                                    "无法获取用户详细数据"
                                            )
                                    );
                        }
                )
                .orElseThrow(() ->
                        new InternalServerException("找不到可用的认证信息")
                );
    }
}
