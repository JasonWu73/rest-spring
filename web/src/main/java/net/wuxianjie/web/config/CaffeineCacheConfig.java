package net.wuxianjie.web.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.core.domain.CachedToken;
import net.wuxianjie.web.constant.TokenAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 配置Caffeine本地缓存
 *
 * @author 吴仙杰
 */
@Configuration
public class CaffeineCacheConfig {

  /**
   * 配置Token专用缓存
   *
   * @return Token专用缓存
   */
  @Bean
  public Cache<String, CachedToken> tokenCache() {
    return Caffeine.newBuilder()
        // 设置在写入缓存的多长时间后自动清除
        .expireAfterWrite(TokenAttributes.TOKEN_EXPIRES_IN_SECONDS, TimeUnit.SECONDS)
        .build();
  }
}
