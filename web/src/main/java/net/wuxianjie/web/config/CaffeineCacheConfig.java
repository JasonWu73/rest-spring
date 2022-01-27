package net.wuxianjie.web.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.core.model.dto.CachedTokenDto;
import net.wuxianjie.web.constant.TokenAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 配置 Caffeine 本地缓存
 *
 * @author 吴仙杰
 */
@Configuration
public class CaffeineCacheConfig {

  /**
   * 配置 Token 专用缓存
   *
   * @return Token 专用缓存
   */
  @Bean
  public Cache<String, CachedTokenDto> tokenCache() {
    return Caffeine.newBuilder()
        // 设置在写入缓存的多长时间后自动清除
        .expireAfterWrite(TokenAttributes.TOKEN_EXPIRES_IN_SECONDS, TimeUnit.SECONDS)
        .build();
  }
}
