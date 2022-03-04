package net.wuxianjie.core.config;

import cn.hutool.core.text.StrSplitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.constant.CommonValues;
import net.wuxianjie.core.dto.ResponseDto;
import net.wuxianjie.core.filter.TokenAuthenticationFilter;
import net.wuxianjie.core.util.ResponseDtoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Qualifier(BeanQualifiers.ALLOWED_ANT_PATHS)
    private final String allowedAntPaths;

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // 参数：被切分字符串，分隔符逗号，0 表示无限制分片数，去除两边空格，忽略空白项
        final String[] paths = StrSplitter.splitToArray(allowedAntPaths, ',', 0, true, true);

        http
                .authorizeRequests()
                // 按顺序比较
                .antMatchers("/access_token", "/refresh_token/{\\.+}")
                .permitAll() // principal 为 "anonymous"
                .antMatchers(paths)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                // 异常
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("身份认证失败：{}", authException.getMessage());

                    final ResponseDto<Void> result = ResponseDtoWrapper.fail("身份认证失败");

                    response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());

                    response.getWriter().write(objectMapper.writeValueAsString(result));
                }) // 401
                // 需由 Spring Security 自己处理 `AccessDeniedException` 异常，
                // 即若有全局异常处理，则不生效
                .accessDeniedHandler(((request, response, accessDeniedException) -> {
                    log.warn("权限认证失败：{}", accessDeniedException.getMessage());

                    final ResponseDto<Void> result = ResponseDtoWrapper.fail("无访问权限");

                    response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
                    response.setStatus(HttpStatus.FORBIDDEN.value());

                    response.getWriter().write(objectMapper.writeValueAsString(result));
                })) // 403
                .and()
                // 禁用 CSRF 措施
                .csrf()
                .disable()
                // 无状态授权机制，每次请求都需要验证 Token，
                // 故不需要设置服务器端 `HttpSession`，
                // 也不需要设置客户端 `JSESSIONID` Cookies
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 解决浏览器发送 OPTIONS 跨域检查请求时返回 401 的问题
                .cors()
                .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                // 添加 Token 身份认证处理过滤器
                .addFilterBefore(
                        tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
    }
}
