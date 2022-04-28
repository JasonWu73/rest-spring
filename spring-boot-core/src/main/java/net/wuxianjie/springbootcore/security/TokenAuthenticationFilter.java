package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.rest.ApiResult;
import net.wuxianjie.springbootcore.rest.ApiResultWrapper;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.util.NetUtils;
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
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse resp,
                                  FilterChain chain) throws IOException, ServletException {
    Optional<String> tokenOptional = getTokenFromRequest(req);

    if (tokenOptional.isEmpty()) {
      chain.doFilter(req, resp);
      return;
    }

    try {
      TokenUserDetails user = authService.authenticate(tokenOptional.get());
      loginToSpringSecurityContext(user);
    } catch (TokenAuthenticationException e) {
      SecurityContextHolder.clearContext();

      sendToResponse(resp, e.getMessage(), HttpStatus.UNAUTHORIZED);
      return;
    } catch (Throwable e) {
      String respMsg = "Token 认证异常";

      log.error(
        "uri={}；client={} -> " + respMsg,
        req.getRequestURI(),
        NetUtils.getRealIpAddress(req),
        e
      );

      SecurityContextHolder.clearContext();

      sendToResponse(resp, respMsg, HttpStatus.INTERNAL_SERVER_ERROR);
      return;
    }

    chain.doFilter(req, resp);
  }

  private Optional<String> getTokenFromRequest(HttpServletRequest req) {
    String bearer = StrUtil.trim(req.getHeader(HttpHeaders.AUTHORIZATION));

    return Optional.ofNullable(bearer)
      .map(s -> {
        if (!StrUtil.startWith(s, BEARER_PREFIX)) {
          return null;
        }

        String token = StrUtil.subAfter(s, BEARER_PREFIX, false);

        return StrUtil.trimToNull(token);
      });
  }

  private void loginToSpringSecurityContext(TokenUserDetails user) {
    List<GrantedAuthority> authorityList = getAuthorities(user.getRoles());

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, authorityList);
    SecurityContextHolder.getContext().setAuthentication(token);
  }

  private List<GrantedAuthority> getAuthorities(String roles) {
    return Optional.ofNullable(StrUtil.trimToNull(roles))
      .map(s -> {
        String commaSeparatedRoles = Arrays.stream(s.split(","))
          .reduce("", (roleOne, roleTwo) -> {
            String appended = ROLE_PREFIX + roleTwo.trim().toUpperCase();

            if (StrUtil.isEmpty(roleOne)) {
              return appended;
            }

            return roleOne + "," + appended;
          });

        return AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedRoles);
      })
      .orElse(Collections.emptyList());
  }

  private void sendToResponse(HttpServletResponse resp,
                              String respMsg,
                              HttpStatus httpStatus) throws IOException {
    resp.setContentType(WebSecurityConfig.APPLICATION_JSON_UTF8_VALUE);
    resp.setStatus(httpStatus.value());

    ApiResult<Void> fail = ApiResultWrapper.fail(respMsg);
    String json = objectMapper.writeValueAsString(fail);

    resp.getWriter().write(json);
  }
}
