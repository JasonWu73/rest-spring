package net.wuxianjie.springbootcore.security;

import cn.hutool.core.text.StrSplitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.rest.ApiResult;
import net.wuxianjie.springbootcore.rest.ApiResultWrapper;
import net.wuxianjie.springbootcore.util.NetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Spring Security 配置。
 *
 * @author 吴仙杰
 */
@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /**
   * 获取 Access Token 的请求路径。
   */
  static final String ACCESS_TOKEN_PATH = "/api/v1/access-token";

  /**
   * 刷新 Access Token 的请求路径前缀，不包含 URL 路径参数。
   */
  static final String REFRESH_TOKEN_PATH_PREFIX = "/api/v1/refresh-token";

  static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

  static final String FAVICON_PATH = "/favicon.ico";

  static final String[] DEFAULT_PERMIT_ALL = {
    ACCESS_TOKEN_PATH,
    REFRESH_TOKEN_PATH_PREFIX + "/{.+}",
    FAVICON_PATH
  };

  private final ObjectMapper objectMapper;
  private final SecurityConfig securityConfig;
  private final TokenAuthenticationFilter authenticationFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    String[] antPatterns = Optional.ofNullable(securityConfig.getPermitAllAntPatterns())
      .map(s -> StrSplitter.splitToArray(s, ',', 0, true, true))
      .orElse(new String[]{});

    http.authorizeRequests()
      .antMatchers(DEFAULT_PERMIT_ALL)
      .permitAll() // principal 为 anonymous
      .antMatchers(antPatterns)
      .permitAll()
      // 顺序很重要：匹配全部请求的配置必须位于最后
      .anyRequest()
      .authenticated()
      .and()
      // 异常
      .exceptionHandling()
      // 401
      .authenticationEntryPoint((req, resp, authException) -> {
        String respMsg = "Token 认证失败";

        logWarn(req, respMsg);

        resp.setContentType(APPLICATION_JSON_UTF8_VALUE);
        resp.setStatus(HttpStatus.UNAUTHORIZED.value());

        ApiResult<Void> fail = ApiResultWrapper.fail(respMsg);
        String json = objectMapper.writeValueAsString(fail);

        resp.getWriter().write(json);
      })
      // 403
      // 需由 Spring Security 自己处理 AccessDeniedException 异常，否则以下配置不生效
      .accessDeniedHandler((req, resp, deniedException) -> {
        String respMsg = "Token 未授权";

        logWarn(req, respMsg);

        resp.setContentType(APPLICATION_JSON_UTF8_VALUE);
        resp.setStatus(HttpStatus.FORBIDDEN.value());

        ApiResult<Void> fail = ApiResultWrapper.fail(respMsg);
        String json = objectMapper.writeValueAsString(fail);

        resp.getWriter().write(json);
      })
      .and()
      // 禁用 CSRF 措施
      .csrf()
      .disable()
      // 无状态鉴权机制，每次请求都需要 Token 认证，故不需要设置服务器端 HttpSession，也不需要设置客户端 JSESSIONID Cookies
      .sessionManagement()
      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      // 解决浏览器发送 OPTIONS 跨域检查请求时返回 401 的问题
      .cors()
      .configurationSource(req -> new CorsConfiguration().applyPermitDefaultValues())
      .and()
      // 添加 Token 认证过滤器
      .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
  }

  /**
   * 密码哈希编码器。
   *
   * @return {@link PasswordEncoder}
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private void logWarn(HttpServletRequest req, String respMsg) {
    log.warn(
      "uri={}；client={} -> {}",
      req.getRequestURI(),
      NetUtils.getRealIpAddress(req),
      respMsg
    );
  }
}
