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
public class TokenAuthFilter extends OncePerRequestFilter {

  /**
   * HTTP Header: {@code Authorization: Bearer {{accessToken}}}.
   */
  private static final String BEARER_PREFIX = "Bearer ";

  /**
   * Spring Security 要求角色名必须是大写，且以 ROLE_ 为前缀
   */
  private static final String ROLE_PREFIX = "ROLE_";

  private final ObjectMapper objectMapper;
  private final TokenAuthService tokenAuthService;

  @Override
  protected void doFilterInternal(HttpServletRequest req,
                                  HttpServletResponse resp,
                                  FilterChain chain) throws IOException, ServletException {
    Optional<String> tokenOpt = getTokenFromReq(req);
    if (tokenOpt.isEmpty()) {
      chain.doFilter(req, resp);
      return;
    }

    try {
      TokenUserDetails user = tokenAuthService.authenticate(tokenOpt.get());
      loginToSpringSecurityContext(user);
    } catch (TokenAuthenticationException e) {
      SecurityContextHolder.clearContext();

      sendToResp(resp, e.getMessage(), HttpStatus.UNAUTHORIZED);
      return;
    } catch (Throwable e) {
      String respMsg = "Token 身份验证异常";
      log.error(
        respMsg + "，请求 IP 为 {}，请求路径为 {}",
        NetUtils.getRealIpAddr(req),
        req.getRequestURI(),
        e
      );

      SecurityContextHolder.clearContext();

      sendToResp(resp, respMsg, HttpStatus.INTERNAL_SERVER_ERROR);
      return;
    }

    chain.doFilter(req, resp);
  }

  /**
   * 输出至响应体中。
   *
   * @param resp       {@link  HttpServletRequest}
   * @param respMsg    提示信息
   * @param httpStatus HTTP 状态码
   * @throws IOException 当写入响应体异常时抛出
   */
  public void sendToResp(HttpServletResponse resp,
                         String respMsg,
                         HttpStatus httpStatus) throws IOException {
    resp.setContentType(WebSecurityConfig.APPLICATION_JSON_UTF8_VALUE);
    resp.setStatus(httpStatus.value());

    String json = objectMapper.writeValueAsString(ApiResultWrapper.fail(respMsg));
    resp.getWriter().write(json);
  }

  private Optional<String> getTokenFromReq(HttpServletRequest req) {
    return Optional.ofNullable(StrUtil.trim(req.getHeader(HttpHeaders.AUTHORIZATION)))
      .map(bearer -> {
        boolean isNotBearerStr = !StrUtil.startWith(bearer, BEARER_PREFIX);
        if (isNotBearerStr) return null;

        return StrUtil.trimToNull(StrUtil.subAfter(bearer, BEARER_PREFIX, false));
      });
  }

  private void loginToSpringSecurityContext(TokenUserDetails user) {
    List<GrantedAuthority> authorities = getAuthorities(user.getRoles());
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(token);
  }

  private List<GrantedAuthority> getAuthorities(String roles) {
    return Optional.ofNullable(StrUtil.trimToNull(roles))
      .map(notNullRoles -> {
        String commaSeparatedRoles = Arrays.stream(notNullRoles.split(","))
          .reduce("", (roleOne, roleTwo) -> {
            String appended = ROLE_PREFIX + roleTwo.trim().toUpperCase();

            if (StrUtil.isEmpty(roleOne)) return appended;

            return roleOne + "," + appended;
          });

        return AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedRoles);
      })
      .orElse(Collections.emptyList());
  }
}
