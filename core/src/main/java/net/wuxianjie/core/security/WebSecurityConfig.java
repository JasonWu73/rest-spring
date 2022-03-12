package net.wuxianjie.core.security;

import cn.hutool.core.text.StrSplitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.rest.RestData;
import net.wuxianjie.core.rest.RestDataWrapper;
import net.wuxianjie.core.shared.CommonValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * 配置 Spring Security
 */
@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final ObjectMapper objectMapper;
  private final SecurityConfigData securityConfig;
  private final TokenAuthenticationFilter tokenAuthenticationFilter;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    final String antPatterns = securityConfig.getPermitAllAntPatterns();
    final String[] permitAllAntPatterns;

    if (antPatterns == null) {
      permitAllAntPatterns = new String[]{};
    } else {
      // 参数：被切分字符串，分隔符逗号，0 表示无限制分片数，去除两边空格，忽略空白项
      permitAllAntPatterns = StrSplitter.splitToArray(
        antPatterns,
        ',',
        0,
        true,
        true
      );
    }


    http
      .authorizeRequests()
      // 按顺序比较
        .antMatchers(
        "/api/v1/access_token",
        "/api/v1/refresh_token/{\\.+}"
        )
          .permitAll() // principal 为 anonymous
        .antMatchers(permitAllAntPatterns)
          .permitAll()
        .anyRequest()
          .authenticated()
      .and()
        // 异常
        .exceptionHandling()
          // 401
          .authenticationEntryPoint((request, response, authException) -> {
            log.warn("Token 认证失败：{}", authException.getMessage());

            final RestData<Void> data =
              RestDataWrapper.fail("Token 认证失败");

            response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            response.getWriter().write(objectMapper.writeValueAsString(data));
          })
          // 403
          // 需由 Spring Security 自己处理 AccessDeniedException 异常，否则不生效
          .accessDeniedHandler(((request, response, deniedException) -> {
            log.warn("Token 未授权：{}", deniedException.getMessage());

            final RestData<Void> data =
              RestDataWrapper.fail("Token 未授权");

            response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());

            response.getWriter().write(objectMapper.writeValueAsString(data));
          }))
      .and()
        // 禁用 CSRF 措施
        .csrf()
          .disable()
        // 无状态鉴权机制，每次请求都需要 Token 认证，
        // 故不需要设置服务器端 HttpSession，
        // 也不需要设置客户端 JSESSIONID Cookies
        .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
        // 解决浏览器发送 OPTIONS 跨域检查请求时返回 401 的问题
        .cors()
          .configurationSource(request ->
            new CorsConfiguration().applyPermitDefaultValues()
          )
      .and()
        // 添加 Token 认证过滤器
        .addFilterBefore(
          tokenAuthenticationFilter,
          UsernamePasswordAuthenticationFilter.class
        );
  }
}
