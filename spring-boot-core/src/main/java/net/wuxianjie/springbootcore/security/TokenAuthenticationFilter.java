package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.rest.ApiResultWrapper;
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
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * HTTP Header: {@code Authorization: Bearer {{accessToken}}}.
     */
    private static final String AUTHORIZATION_BEARER_PREFIX = "Bearer ";

    private static final String SPRING_SECURITY_ROLE_PREFIX = "ROLE_";

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {
        Optional<String> tokenOptional = getTokenFromRequest(request);

        if (tokenOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            TokenUserDetails userDetails =
                    authenticationService.authenticate(tokenOptional.get());

            loginToSpringSecurityContext(userDetails);
        } catch (TokenAuthenticationException e) {
            SecurityContextHolder.clearContext();

            sendToResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
            return;
        } catch (Throwable e) {
            log.error("Token 认证异常", e);

            SecurityContextHolder.clearContext();

            sendToResponse(response, e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendToResponse(
            HttpServletResponse response,
            String message,
            HttpStatus httpStatus
    ) throws IOException {
        response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(httpStatus.value());

        response.getWriter().write(
                objectMapper.writeValueAsString(ApiResultWrapper.fail(message))
        );
    }

    private void loginToSpringSecurityContext(TokenUserDetails userDetails) {
        List<GrantedAuthority> authorityList =
                getAuthorities(userDetails.getAccountRoles());

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorityList
                );

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private List<GrantedAuthority> getAuthorities(String roleStr) {
        List<GrantedAuthority> authorityList;

        if (StrUtil.isEmpty(roleStr)) {
            authorityList = Collections.emptyList();
        } else {
            String roles = Arrays.stream(roleStr.split(","))
                    .reduce("", (roleOne, roleTwo) -> {
                                // Spring Security 要求角色名必须是大写，且以 ROLE_ 为前缀
                                String roleToAppend =
                                        SPRING_SECURITY_ROLE_PREFIX +
                                                roleTwo.trim().toUpperCase();

                                if (StrUtil.isEmpty(roleOne)) {
                                    return roleToAppend;
                                }

                                return roleOne + "," + roleToAppend;
                            }
                    );

            authorityList =
                    AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
        }

        return authorityList;
    }

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        String bearerValue = StrUtil.trim(
                request.getHeader(HttpHeaders.AUTHORIZATION)
        );

        if (bearerValue == null
                || !bearerValue.startsWith(AUTHORIZATION_BEARER_PREFIX)
        ) {
            return Optional.empty();
        }

        String token = StrUtil.subAfter(
                bearerValue, AUTHORIZATION_BEARER_PREFIX, false
        );

        return Optional.of(token);
    }
}
