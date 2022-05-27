package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.exception.TokenAuthenticationException;
import net.wuxianjie.springbootcore.rest.ApiResultWrapper;
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
 * Token 身份验证过滤器。
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
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * Spring Security 要求角色名必须是大写，且以 ROLE_ 为前缀
   */
  private static final String ROLE_PREFIX = "ROLE_";

  private final ObjectMapper objectMapper;
  private final TokenService tokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws IOException, ServletException {
    Optional<String> tokenOpt = getTokenFromRequestHeader(request);
    if (tokenOpt.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      TokenUserDetails user = tokenService.authenticate(tokenOpt.get());
      loginToSpringSecurityContext(user);
    } catch (TokenAuthenticationException e) {
      SecurityContextHolder.clearContext();

      sendToResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
      return;
    } catch (Throwable e) {
      String message = "Token 身份验证异常";
      log.error(
        message + "，客户端信息：uri={};client={}",
        request.getRequestURI(),
        NetUtils.getRealIpAddress(request),
        e
      );

      SecurityContextHolder.clearContext();

      sendToResponse(response, message, HttpStatus.INTERNAL_SERVER_ERROR);
      return;
    }

    filterChain.doFilter(request, response);
  }

  /**
   * 输出至响应体中。
   *
   * @param response   {@link  HttpServletRequest}
   * @param message    提示信息
   * @param httpStatus HTTP 状态码
   * @throws IOException 当写入响应体异常时抛出
   */
  public void sendToResponse(HttpServletResponse response,
                             String message,
                             HttpStatus httpStatus) throws IOException {
    response.setContentType(WebSecurityConfig.APPLICATION_JSON_UTF8_VALUE);
    response.setStatus(httpStatus.value());

    String json = objectMapper.writeValueAsString(ApiResultWrapper.fail(message));
    response.getWriter().write(json);
  }

  private Optional<String> getTokenFromRequestHeader(HttpServletRequest request) {
    return Optional.ofNullable(StrUtil.trim(request.getHeader(HttpHeaders.AUTHORIZATION)))
      .map(bearer -> {
        boolean isNotBearerString = !StrUtil.startWith(bearer, BEARER_PREFIX);
        if (isNotBearerString) return null;

        return StrUtil.trimToNull(StrUtil.subAfter(bearer, BEARER_PREFIX, false));
      });
  }

  private void loginToSpringSecurityContext(TokenUserDetails user) {
    List<GrantedAuthority> authorities = getAuthorities(user.getRoles());
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(token);
  }

  private List<GrantedAuthority> getAuthorities(String commaSeparatedRoles) {
    return Optional.ofNullable(StrUtil.trimToNull(commaSeparatedRoles))
      .map(r -> {
        String roles = Arrays.stream(r.split(","))
          .reduce("", (roleOne, roleTwo) -> {
            String appended = ROLE_PREFIX + roleTwo.strip().toUpperCase();

            if (StrUtil.isEmpty(roleOne)) return appended;

            return roleOne + "," + appended;
          });

        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
      })
      .orElse(Collections.emptyList());
  }
}
