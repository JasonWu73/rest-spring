package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.rest.RestData;
import net.wuxianjie.springbootcore.rest.RestDataWrapper;
import net.wuxianjie.springbootcore.shared.CommonValues;
import net.wuxianjie.springbootcore.shared.TokenAuthenticationException;
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
    private static final String AUTHORIZATION_BEARER_PREFIX = "Bearer ";

    private static final String SPRING_SECURITY_ROLE_PREFIX = "ROLE_";

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        Optional<String> tokenOptional = getTokenFromRequest(request);
        if (tokenOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            TokenUserDetails userDetails = authenticationService.authenticate(tokenOptional.get());
            login2SpringSecurityContext(userDetails);
        } catch (TokenAuthenticationException e) {
            SecurityContextHolder.clearContext();

            send2Response(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        } catch (Throwable e) {
            log.warn("Token 认证异常", e);

            SecurityContextHolder.clearContext();

            send2Response(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void send2Response(HttpServletResponse response, String message, HttpStatus httpStatus) throws IOException {
        response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(httpStatus.value());
        RestData<Void> data = RestDataWrapper.fail(message);
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

    private void login2SpringSecurityContext(TokenUserDetails userDetails) {
        List<GrantedAuthority> authorities = getAuthorities(userDetails.getAccountRoles());
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private List<GrantedAuthority> getAuthorities(String commaSeparatedRoles) {
        List<GrantedAuthority> authorities;

        if (StrUtil.isNotEmpty(commaSeparatedRoles)) {
            String[] roleArray = commaSeparatedRoles.split(",");
            String commaSeparatedSpringSecurityRoles = Arrays.stream(roleArray)
                    .reduce("", (roleOne, roleTwo) -> {
                        // Spring Security 要求角色名必须是大写，且以 ROLE_ 为前缀
                        String role = SPRING_SECURITY_ROLE_PREFIX + roleTwo.trim().toUpperCase();
                        if (StrUtil.isEmpty(roleOne)) {
                            return role;
                        }
                        return roleOne + "," + role;
                    });

            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedSpringSecurityRoles);
        } else {
            authorities = Collections.emptyList();
        }

        return authorities;
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String bearerValue = StrUtil.trim(request.getHeader(HttpHeaders.AUTHORIZATION));
        if (bearerValue == null || !bearerValue.startsWith(AUTHORIZATION_BEARER_PREFIX)) {
            return Optional.empty();
        }

        int tokenBeginIndexInclusive = AUTHORIZATION_BEARER_PREFIX.length();
        String token = bearerValue.substring(tokenBeginIndexInclusive);
        return Optional.of(token);
    }
}
