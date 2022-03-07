package net.wuxianjie.core.rest.auth.config;

import cn.hutool.core.text.StrSplitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.rest.ResponseDto;
import net.wuxianjie.core.rest.ResponseWrapper;
import net.wuxianjie.core.rest.auth.TokenAuthenticationFilter;
import net.wuxianjie.core.rest.auth.dto.AuthConfigDto;
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

    private final AuthConfigDto authConfig;
    private final ObjectMapper objectMapper;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // 参数：被切分字符串，分隔符逗号，0 表示无限制分片数，去除两边空格，忽略空白项
        final String[] paths = StrSplitter.splitToArray(
                authConfig.getAllowedAntPaths(),
                ',',
                0,
                true,
                true
        );

        http
                .authorizeRequests()
                // 按顺序比较
                .antMatchers(
                        "/api/v1/access_token",
                        "/api/v1/refresh_token/{\\.+}"
                )
                .permitAll() // principal 为 "anonymous"
                .antMatchers(paths)
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                // 异常
                .exceptionHandling()
                .authenticationEntryPoint((req, res, e) -> {
                    log.warn("身份认证失败：{}", e.getMessage());

                    final ResponseDto<Void> result =
                            ResponseWrapper.fail("身份认证失败");

                    res.setContentType(
                            CommonValues.APPLICATION_JSON_UTF8_VALUE
                    );
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());

                    res.getWriter()
                            .write(objectMapper.writeValueAsString(result));
                }) // 401
                // 需由 Spring Security 自己处理 `AccessDeniedException` 异常，
                // 即若有全局异常处理，则不生效
                .accessDeniedHandler(((req, res, e) -> {
                    log.warn("权限认证失败：{}", e.getMessage());

                    final ResponseDto<Void> result =
                            ResponseWrapper.fail("无访问权限");

                    res.setContentType(
                            CommonValues.APPLICATION_JSON_UTF8_VALUE
                    );
                    res.setStatus(HttpStatus.FORBIDDEN.value());

                    res.getWriter()
                            .write(objectMapper.writeValueAsString(result));
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
                .configurationSource(request ->
                        new CorsConfiguration().applyPermitDefaultValues())
                .and()
                // 添加 Token 身份认证处理过滤器
                .addFilterBefore(
                        tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
    }
}
