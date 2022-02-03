package net.wuxianjie.core.config;

import cn.hutool.core.text.StrSplitter;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.constant.BeanQualifiers;
import net.wuxianjie.core.constant.Mappings;
import net.wuxianjie.core.filter.TokenAuthenticationFailHandler;
import net.wuxianjie.core.filter.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

/**
 * 定制 Spring Security
 *
 * @author 吴仙杰
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final TokenAuthenticationFailHandler authenticationFailHandler;
  private final TokenAuthenticationFilter tokenAuthenticationFilter;
  @Qualifier(BeanQualifiers.ALLOWED_ANT_PATHS) private final String allowedAntPaths;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // 参数: 被切分字符串, 分隔符逗号, 0表示无限制分片数, 去除两边空格, 忽略空白项
    final String[] paths = StrSplitter.splitToArray(allowedAntPaths, ',', 0, true, true);

    http
        // 授权
        .authorizeRequests()
        // 按顺序比较
        .antMatchers(
            Mappings.ACCESS_TOKEN,
            Mappings.REFRESH_TOKEN
        ).permitAll() // principal 为 "anonymous"
        .antMatchers(paths).permitAll()
        .anyRequest().authenticated()
        .and()
        // 异常
        .exceptionHandling()
        .authenticationEntryPoint(authenticationFailHandler) // 401
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
        // 解决浏览器发送 OPTIONS 跨域检查请求时返回401的问题
        .cors()
        .configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
        .and()
        // 添加 Token 身份认证处理过滤器
        .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
