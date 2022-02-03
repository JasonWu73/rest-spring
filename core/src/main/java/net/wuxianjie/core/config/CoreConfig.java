package net.wuxianjie.core.config;

import net.wuxianjie.core.constant.BeanQualifiers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 模块主配置类
 *
 * <p>用于配置自定义 Bean</p>
 *
 * @author 吴仙杰
 */
@Configuration
@PropertySource(value = "classpath:core.yml", factory = YamlPropertySourceFactory.class)
@EnableAspectJAutoProxy
public class CoreConfig {

  @Value("${core.jwt-signing-key}")
  private String jwtSigningKey;

  @Value("${core.allowed-ant-paths}")
  private String allowedAntPaths;

  /**
   * 用于生成与校验 JWT 的签名密钥
   *
   * @return JWT 签名密钥
   */
  @Bean(BeanQualifiers.JWT_SIGNING_KEY)
  public String jwtSingingKey() {
    return jwtSigningKey;
  }

  /**
   * 任何人都可访问的请求路径, 支持 {@code AntPathRequestMatcher} 模式, 多个路径以 {@code ,} 分隔
   *
   * @return 无需鉴权的请求路径列表
   */
  @Bean(BeanQualifiers.ALLOWED_ANT_PATHS)
  public String allowedAntPaths() {
    return allowedAntPaths;
  }

  /**
   * 密码编码器
   *
   * @return 密码编码器
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
