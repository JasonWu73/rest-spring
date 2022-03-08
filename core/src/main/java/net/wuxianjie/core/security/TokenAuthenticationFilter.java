package net.wuxianjie.core.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.rest.RestData;
import net.wuxianjie.core.rest.RestDataWrapper;
import net.wuxianjie.core.shared.CommonValues;
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
    private final TokenAuthenticationService tokenAuthenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String accessToken = getAccessTokenFromHttpRequest(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            TokenUserDetails cachedToken = tokenAuthenticationService.authenticate(accessToken);

            login2SpringSecurityContext(cachedToken);
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

    private String getAccessTokenFromHttpRequest(HttpServletRequest request) {
        String authorizationHttpHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHttpHeader == null ||
                !authorizationHttpHeader.startsWith(AUTHORIZATION_BEARER_PREFIX)
        ) {
            return null;
        }

        int authorizationValuePrefixLength = AUTHORIZATION_BEARER_PREFIX.length();

        return authorizationHttpHeader.substring(authorizationValuePrefixLength);
    }

    private void login2SpringSecurityContext(TokenUserDetails userDetails) {
        List<GrantedAuthority> grantedAuthorities = getGrantedAuthorityFromString(userDetails.getAccountRoles());

        UsernamePasswordAuthenticationToken authenticatedToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
    }

    private List<GrantedAuthority> getGrantedAuthorityFromString(String accountRoles) {
        List<GrantedAuthority> grantedAuthorities;

        if (StrUtil.isNotEmpty(accountRoles)) {
            String[] roleArray = accountRoles.split(",");

            // Spring Security 要求角色名必须是大写，且以 ROLE_ 为前缀
            String commaSeparatedUpperCasePrefixedStr = Arrays.stream(roleArray)
                    .reduce("", (s1, s2) -> {
                        String springSecurityRoleValue = SPRING_SECURITY_ROLE_PREFIX + s2.trim().toUpperCase();

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

    private void send2HttpResponse(HttpServletResponse response, String message, HttpStatus httpStatus)
            throws IOException {
        RestData<Void> responseData = RestDataWrapper.fail(message);

        response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(httpStatus.value());

        response.getWriter().write(objectMapper.writeValueAsString(responseData));
    }
}
