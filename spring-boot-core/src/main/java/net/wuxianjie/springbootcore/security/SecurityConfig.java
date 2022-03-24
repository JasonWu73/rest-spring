package net.wuxianjie.springbootcore.security;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.rest.ApiResultWrapper;
import net.wuxianjie.springbootcore.shared.CommonValues;
import net.wuxianjie.springbootcore.shared.CoreConfigData;
import net.wuxianjie.springbootcore.shared.YamlSourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
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

import java.util.Optional;

/**
 * 网站安全相关配置。
 *
 * @author 吴仙杰
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@PropertySource(value = "classpath:core.yml", factory = YamlSourceFactory.class)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 获取 Access Token 的请求路径。
     */
    public static final String ACCESS_TOKEN_PATH = "/api/v1/access_token";

    /**
     * 刷新 Access Token 的请求路径前缀，并不包含 URL 路径参数。
     */
    public static final String REFRESH_TOKEN_PATH_PREFIX =
            "/api/v1/refresh_token";

    private static final String FAVICON_PATH = "/favicon.ico";

    private static final String[] DEFAULT_PERMIT_ALL = {
            ACCESS_TOKEN_PATH,
            REFRESH_TOKEN_PATH_PREFIX + "/{\\\\.+}",
            FAVICON_PATH
    };

    @Value("${core.security.jwt-signing-key}")
    private String jwtSigningKey;

    @Value("${core.security.permit-all-ant-patterns}")
    private String permitAllAntPatterns;

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationFilter authenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] antPatterns = Optional.ofNullable(
                        StrUtil.trim(permitAllAntPatterns)
                )
                .map(commaSeparatedPattern ->
                        StrSplitter.splitToArray(commaSeparatedPattern,
                                ',', 0,
                                true, true
                        )
                )
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
                            String messageToResponse = "Token 认证失败";

                            log.warn("{}：{}", messageToResponse,
                                    authException.getMessage()
                            );

                            response.setContentType(
                                    CommonValues.APPLICATION_JSON_UTF8_VALUE
                            );
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());

                            String json = objectMapper.writeValueAsString(
                                    ApiResultWrapper.fail(messageToResponse)
                            );

                            response.getWriter().write(json);
                        }
                )
                // 403
                // 需由 Spring Security 自己处理 AccessDeniedException 异常，否则以下配置不生效
                .accessDeniedHandler((request, response, deniedException) -> {
                            String messageToResponse = "Token 未授权";

                            log.warn("{}：{}", messageToResponse,
                                    deniedException.getMessage()
                            );

                            response.setContentType(
                                    CommonValues.APPLICATION_JSON_UTF8_VALUE
                            );
                            response.setStatus(HttpStatus.FORBIDDEN.value());

                            String json = objectMapper.writeValueAsString(
                                    ApiResultWrapper.fail(messageToResponse)
                            );

                            response.getWriter().write(json);
                        }
                )
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
                .configurationSource(request ->
                        new CorsConfiguration().applyPermitDefaultValues()
                )
                .and()
                // 添加 Token 认证过滤器
                .addFilterBefore(authenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
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

    /**
     * 网站安全相关的配置对象。
     *
     * @return {@link CoreConfigData}
     */
    @Bean
    public CoreConfigData securityConfigData() {
        jwtSigningKey = StrUtil.trimToNull(jwtSigningKey);
        permitAllAntPatterns = StrUtil.trimToNull(permitAllAntPatterns);

        if (jwtSigningKey == null) {
            throw new IllegalArgumentException(
                    "core.security.jwt-signing-key 不能为空"
            );
        }

        return new CoreConfigData(jwtSigningKey, permitAllAntPatterns);
    }
}
