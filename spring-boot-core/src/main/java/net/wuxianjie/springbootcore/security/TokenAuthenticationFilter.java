package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.rest.ApiResultWrapper;
import net.wuxianjie.springbootcore.shared.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.shared.util.NetUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
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
import java.util.Optional;

/**
 * Token 认证过滤器。
 *
 * @author 吴仙杰
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * HTTP Header: {@code Authorization: Bearer {{accessToken}}}.
     */
    static final String BEARER_PREFIX = "Bearer ";

    /**
     * Spring Security 要求角色名必须是大写，且以 ROLE_ 为前缀
     */
    static final String ROLE_PREFIX = "ROLE_";

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationService authService;

    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request,
                                    final @NonNull HttpServletResponse response,
                                    final @NonNull FilterChain filterChain) throws IOException, ServletException {
        final Optional<String> tokenOpt = getTokenFromRequest(request);

        if (tokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final TokenUserDetails userDetails = authService.authenticate(tokenOpt.get());
            loginToSpringSecurityContext(userDetails);
        } catch (TokenAuthenticationException e) {
            SecurityContextHolder.clearContext();

            sendToResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        } catch (Throwable e) {
            log.error("uri={}；client={} -> Token 认证异常",
                    request.getRequestURI(), NetUtils.getRealIpAddress(request), e);

            SecurityContextHolder.clearContext();

            sendToResponse(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> getTokenFromRequest(final HttpServletRequest request) {
        return Optional.ofNullable(StrUtil.trim(request.getHeader(HttpHeaders.AUTHORIZATION)))
                .map(bearer -> {
                    if (!StrUtil.startWith(bearer, BEARER_PREFIX)) return null;

                    final String token = StrUtil.subAfter(bearer, BEARER_PREFIX, false);
                    return StrUtil.trimToNull(token);
                });
    }

    private void loginToSpringSecurityContext(final TokenUserDetails userDetails) {
        final List<GrantedAuthority> authorityList = getAuthorities(userDetails.getRoles());

        final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                userDetails, null, authorityList);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private List<GrantedAuthority> getAuthorities(final String commaSeparatedRole) {
        if (StrUtil.isEmpty(commaSeparatedRole)) return Collections.emptyList();

        final String roles = Arrays.stream(commaSeparatedRole.split(","))
                .reduce("", (roleOne, roleTwo) -> {
                    final String appended = ROLE_PREFIX + roleTwo.trim().toUpperCase();
                    if (StrUtil.isEmpty(roleOne)) return appended;

                    return roleOne + "," + appended;
                });

        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
    }

    private void sendToResponse(final HttpServletResponse response,
                                final String message,
                                final HttpStatus httpStatus) throws IOException {
        response.setContentType(WebSecurityConfig.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(httpStatus.value());

        final String json = objectMapper.writeValueAsString(ApiResultWrapper.fail(message));
        response.getWriter().write(json);
    }
}
