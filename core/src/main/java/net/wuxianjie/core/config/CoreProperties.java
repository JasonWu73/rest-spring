package net.wuxianjie.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义属性的属性配置类
 *
 * @author 吴仙杰
 * @see <a href="https://docs.spring.io/spring-boot/docs/2.6.3/reference/html/configuration-metadata.html#configuration-metadata.annotation-processor">Configuration Metadata</a>
 * @see <a href="https://www.baeldung.com/spring-boot-configuration-metadata">A Guide to Spring Boot Configuration Metadata | Baeldung</a>
 */
@Configuration
@ConfigurationProperties(prefix = "core")
@Getter
@Setter
public class CoreProperties {

  /** 默认密码{@code 123} */
  public static final String JWT_SIGNING_KEY = "$2a$10$tGF3JMPSHpLMG5LEpDZBy.QFH8lyNDMEeHb6hlacuUMuczAI2H2w6";

  /** 默认开放所有用户可访问的请求路径 */
  public static final String ALLOWED_ANT_PATHS = "/public/**";

  /**
   * 用于生成与验证JWT的签名密钥
   *
   * <p>默认密码（123）：$2a$10$tGF3JMPSHpLMG5LEpDZBy.QFH8lyNDMEeHb6hlacuUMuczAI2H2w6</p>
   */
  private String jwtSigningKey = JWT_SIGNING_KEY;

  /**
   * 任何人都可访问的请求路径，支持AntPathRequestMatcher模式，多个路径以英文逗号,分隔
   *
   * <p>默认放开访问认证的请求路径为：/public/**</p>
   */
  private String allowedAntPaths = ALLOWED_ANT_PATHS;
}
