package net.wuxianjie.springbootcore.security;

import cn.hutool.core.text.StrSplitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.rest.ApiResultWrapper;
import net.wuxianjie.springbootcore.shared.util.NetUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Spring Security 配置。
 *
 * @author 吴仙杰
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
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
            REFRESH_TOKEN_PATH_PREFIX + "/{\\\\.+}",
            FAVICON_PATH
    };

    private final ObjectMapper objectMapper;
    private final SecurityConfigData securityConfigData;
    private final TokenAuthenticationFilter authenticationFilter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        final String[] antPatterns = Optional.ofNullable(securityConfigData.getPermitAllAntPatterns())
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
                .authenticationEntryPoint((request, response, authException) -> {
                    final String msg = "Token 认证失败";

                    logWarn(request, msg);

                    response.setContentType(APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());

                    String json = objectMapper.writeValueAsString(ApiResultWrapper.fail(msg));
                    response.getWriter().write(json);
                })
                // 403
                // 需由 Spring Security 自己处理 AccessDeniedException 异常，否则以下配置不生效
                .accessDeniedHandler((request, response, deniedException) -> {
                    final String msg = "Token 未授权";

                    logWarn(request, msg);

                    response.setContentType(APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.FORBIDDEN.value());

                    String json = objectMapper.writeValueAsString(ApiResultWrapper.fail(msg));
                    response.getWriter().write(json);
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
                .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                // 添加 Token 认证过滤器
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private void logWarn(final HttpServletRequest request,
                         final String message) {
        log.warn("uri={}；client={} -> {}",
                request.getRequestURI(), NetUtils.getRealIpAddress(request), message);
    }
}
