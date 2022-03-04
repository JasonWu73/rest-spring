package net.wuxianjie.core.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.constant.CommonValues;
import net.wuxianjie.core.constant.Prefixes;
import net.wuxianjie.core.dto.PrincipalDto;
import net.wuxianjie.core.dto.ResponseDto;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.service.TokenAuthenticationService;
import net.wuxianjie.core.util.ResponseDtoWrapper;
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
    private final TokenAuthenticationService tokenAuthenticationService;

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
            final PrincipalDto cachedToken = tokenAuthenticationService.authenticate(token);

            login2SpringSecurity(cachedToken);
        } catch (TokenAuthenticationException e) {
            SecurityContextHolder.clearContext();

            send2Response(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);

            SecurityContextHolder.clearContext();

            send2Response(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(final HttpServletRequest request)
            throws TokenAuthenticationException {
        final String bearerHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (bearerHeader == null ||
                !bearerHeader.startsWith(Prefixes.AUTHORIZATION_BEARER)
        ) {
            return null;
        }

        return bearerHeader.substring(Prefixes.AUTHORIZATION_BEARER.length());
    }

    private void login2SpringSecurity(PrincipalDto cachedToken) {
        final List<GrantedAuthority> authorities;

        if (StrUtil.isNotEmpty(cachedToken.getRoles())) {
            final String[] roleArray = cachedToken.getRoles().split(",");

            // Spring Security 要求角色名必须是大写，且以 `ROLE_` 为前缀
            final String roles = Arrays.stream(roleArray)
                    .reduce("", (role1, role2) -> {
                        final String springSecurityRole = Prefixes.ROLES + role2.trim().toUpperCase();

                        if (StrUtil.isNotEmpty(role1)) {
                            return role1 + "," + springSecurityRole;
                        }

                        return springSecurityRole;
                    });

            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
        } else {
            authorities = Collections.emptyList();
        }

        final UsernamePasswordAuthenticationToken passedToken =
                new UsernamePasswordAuthenticationToken(cachedToken, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(passedToken);
    }

    private void send2Response(
            final HttpServletResponse response,
            final String message,
            final HttpStatus httpStatus
    ) throws IOException {
        final ResponseDto<Void> result = ResponseDtoWrapper.fail(message);

        response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(httpStatus.value());

        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
