package net.wuxianjie.core.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.rest.RestData;
import net.wuxianjie.core.rest.RestDataWrapper;
import net.wuxianjie.core.shared.CommonValues;
import net.wuxianjie.core.shared.TokenAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
 * Token 认证实现机制的过滤器。
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * HTTP Header: {@code Authorization: Bearer {{accessToken}}}
     */
    private static final String AUTHORIZATION_BEARER_PREFIX = "Bearer ";

    private static final String SPRING_SECURITY_ROLE_PREFIX = "ROLE_";

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws IOException, ServletException {
        final String accessToken = getAccessTokenFromHttpRequest(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final TokenUserDetails userDetails = authenticationService.authenticate(accessToken);

            login2SpringSecurityContext(userDetails);
        } catch (TokenAuthenticationException e) {
            SecurityContextHolder.clearContext();

            send2HttpResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        } catch (Throwable e) {
            log.warn("Token 认证异常", e);

            SecurityContextHolder.clearContext();

            send2HttpResponse(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Nullable
    private String getAccessTokenFromHttpRequest(HttpServletRequest request) {
        final String authorizationRequestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationRequestHeader == null || !authorizationRequestHeader.startsWith(AUTHORIZATION_BEARER_PREFIX)) {
            return null;
        }

        final int authorizationValuePrefixLength = AUTHORIZATION_BEARER_PREFIX.length();

        return authorizationRequestHeader.substring(authorizationValuePrefixLength);
    }

    private void login2SpringSecurityContext(TokenUserDetails userDetails) {
        final List<GrantedAuthority> grantedAuthorities = getAuthoritiesFromStr(userDetails.getAccountRoles());

        final UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
    }

    private void send2HttpResponse(HttpServletResponse response, String message, HttpStatus httpStatus)
            throws IOException {
        final RestData<Void> responseData = RestDataWrapper.fail(message);

        response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(httpStatus.value());

        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }

    @NonNull
    private List<GrantedAuthority> getAuthoritiesFromStr(String accountRoles) {
        final List<GrantedAuthority> grantedAuthorities;

        if (StrUtil.isNotEmpty(accountRoles)) {
            final String[] roleArray = accountRoles.split(",");

            // Spring Security 要求角色名必须是大写，且以 ROLE_ 为前缀
            final String commaSeparatedUpperCasePrefixedStr = Arrays.stream(roleArray)
                    .reduce("", (s1, s2) -> {
                        final String springSecurityRoleValue = SPRING_SECURITY_ROLE_PREFIX + s2.trim().toUpperCase();

                        if (StrUtil.isNotEmpty(s1)) {
                            return s1 + "," + springSecurityRoleValue;
                        }

                        return springSecurityRoleValue;
                    });

            grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedUpperCasePrefixedStr);
        } else {
            grantedAuthorities = Collections.emptyList();
        }

        return grantedAuthorities;
    }
}
