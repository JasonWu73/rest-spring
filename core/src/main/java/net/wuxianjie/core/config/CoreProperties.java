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

  public static final String JWT_SIGNING_KEY = "6/ATA2JKtDzT0jhs+loVxzaGiwROIn4bThvdhAIn5wo=";
  public static final String ALLOWED_ANT_PATHS = "/public/**";

  /**
   * 用于生成与验证 JWT 的签名密钥
   *
   * <p>默认 {@code 6/ATA2JKtDzT0jhs+loVxzaGiwROIn4bThvdhAIn5wo=}</p>
   */
  private String jwtSigningKey = JWT_SIGNING_KEY;

  /**
   * 任何人都可访问的请求路径, 支持 {@code AntPathRequestMatcher} 模式, 多个路径以 {@code ,} 分隔
   *
   * <p>默认请求路径 {@code /public} 及 {@code /public/*} 下的资源全部为开放的公开资源</p>
   */
  private String allowedAntPaths = ALLOWED_ANT_PATHS;
}
