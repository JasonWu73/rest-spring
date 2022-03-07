package net.wuxianjie.core.rest.auth;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.rest.ResponseDto;
import net.wuxianjie.core.rest.ResponseWrapper;
import net.wuxianjie.core.rest.auth.dto.PrincipalDto;
import net.wuxianjie.core.shared.CommonValues;
import net.wuxianjie.core.shared.exception.TokenAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 实现 Token 认证机制的过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationService authServer;

    @Override
    protected void doFilterInternal(
            @NonNull final HttpServletRequest request,
            @NonNull final HttpServletResponse response,
            @NonNull final FilterChain filterChain
    ) throws IOException, ServletException {

        final String token = getTokenFromRequest(request);

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final PrincipalDto cachedToken = authServer.authenticate(token);

            login2SpringSecurity(cachedToken);
        } catch (TokenAuthenticationException e) {
            SecurityContextHolder.clearContext();

            send2Response(
                    response,
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED
            );
            return;
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            SecurityContextHolder.clearContext();

            send2Response(
                    response,
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(final HttpServletRequest request)
            throws TokenAuthenticationException {
        final String bearerHeader = request
                .getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerHeader == null ||
                !bearerHeader.startsWith(AuthPrefixes.AUTHORIZATION_BEARER)
        ) {
            return null;
        }

        final int prefixLength = AuthPrefixes.AUTHORIZATION_BEARER.length();

        return bearerHeader.substring(prefixLength);
    }

    private void login2SpringSecurity(PrincipalDto cachedToken) {
        final List<GrantedAuthority> authorities;

        if (StrUtil.isNotEmpty(cachedToken.getRoles())) {
            final String[] roleArray = cachedToken.getRoles().split(",");

            // Spring Security 要求角色名必须是大写，且以 `ROLE_` 为前缀
            final String roles = Arrays.stream(roleArray)
                    .reduce("", (role1, role2) -> {
                        final String springSecurityRole =
                                AuthPrefixes.ROLE + role2.trim().toUpperCase();

                        if (StrUtil.isNotEmpty(role1)) {
                            return role1 + "," + springSecurityRole;
                        }

                        return springSecurityRole;
                    });

            authorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(roles);
        } else {
            authorities = Collections.emptyList();
        }

        final UsernamePasswordAuthenticationToken passedToken =
                new UsernamePasswordAuthenticationToken(
                        cachedToken,
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(passedToken);
    }

    private void send2Response(
            final HttpServletResponse response,
            final String message,
            final HttpStatus httpStatus
    ) throws IOException {
        final ResponseDto<Void> result = ResponseWrapper.fail(message);

        response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(httpStatus.value());

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
