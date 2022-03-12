package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.core.security.TokenUserDetails;
import net.wuxianjie.web.shared.BeanQualifiers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class TokenCacheConfig {

  /**
   * Key: {@code username}
   */
  @Bean(BeanQualifiers.TOKEN_CACHE)
  public Cache<String, TokenUserDetails> tokenCache() {
    return Caffeine.newBuilder()
      .expireAfterWrite(
        TokenAttributes.EXPIRES_IN_SECONDS_VALUE,
        TimeUnit.SECONDS
      )
      .build();
  }
}
