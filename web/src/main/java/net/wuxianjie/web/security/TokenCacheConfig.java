package net.wuxianjie.web.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.web.user.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Token 缓存配置类。
 *
 * @author 吴仙杰
 */
@Configuration
public class TokenCacheConfig {

  /**
   * Token 本地缓存。
   *
   * @return {username : {@link CustomUserDetails}}
   */
  @Bean
  public Cache<String, CustomUserDetails> tokenCache() {
    return Caffeine.newBuilder()
      .expireAfterWrite(TokenAttributes.EXPIRES_IN_SECONDS_VALUE, TimeUnit.SECONDS)
      .build();
  }
}
