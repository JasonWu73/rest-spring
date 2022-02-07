package net.wuxianjie.core.filter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.constant.Prefixes;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.model.dto.RestDto;
import net.wuxianjie.core.model.dto.CachedTokenDto;
import net.wuxianjie.core.service.TokenAuthenticationService;
import net.wuxianjie.core.util.ResponseResultWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.List;
import java.util.Locale;

/**
 * 实现 Token 鉴权认证机制的过滤器
 *
 * @author 吴仙杰
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private final TokenAuthenticationService tokenAuthenticationService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws IOException, ServletException {

    final String token = loadTokenFromRequest(request);

    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      final CachedTokenDto tokenDto = tokenAuthenticationService.authenticate(token);

      login2SpringSecurity(tokenDto);
    }
    catch (TokenAuthenticationException e) {
      // 清除当前线程中的 Spring Security 上下文内容
      SecurityContextHolder.clearContext();

      send2Response(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
      return;
    }
    catch (Throwable e) {
      log.warn(e.getMessage(), e);

      // 清除当前线程中的 Spring Security 上下文内容
      SecurityContextHolder.clearContext();

      send2Response(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String loadTokenFromRequest(final HttpServletRequest request) throws TokenAuthenticationException {

    final String bearerHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (bearerHeader == null || !bearerHeader.startsWith(Prefixes.AUTHORIZATION_BEARER)) {
      return null;
    }

    return bearerHeader.substring(Prefixes.AUTHORIZATION_BEARER.length());
  }

  private void login2SpringSecurity(CachedTokenDto tokenDto) {
    // 注意: Spring Security 要求角色名必须是大写, 且以 `ROLE_` 为前缀
    final String roles = Arrays.stream(tokenDto.getRoles().split(","))
        .reduce("", (s, s2) -> {
          if (StrUtil.isNotEmpty(s)) {
          return s + "," + Prefixes.ROLES + s2.trim().toUpperCase(Locale.ROOT);
        }

        return Prefixes.ROLES + s2.trim().toUpperCase(Locale.ROOT);
      });

    final List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(roles);

    final UsernamePasswordAuthenticationToken passedToken = new UsernamePasswordAuthenticationToken(tokenDto, null, authorities);

    SecurityContextHolder.getContext().setAuthentication(passedToken);
  }

  private void send2Response(final HttpServletResponse response, final String message, final HttpStatus httpStatus) throws IOException {

    final RestDto<Void> result = ResponseResultWrappers.fail(message);

    //noinspection deprecation
    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    response.setStatus(httpStatus.value());

    response.getWriter().write(objectMapper.writeValueAsString(result));
  }
}
