package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Token 缓存配置。
 *
 * @author 吴仙杰
 */
@Configuration
public class TokenCacheConfig {

    /**
     * Token 本地缓存。
     *
     * @return {@code username: TokenUserDetails}
     */
    @Bean
    public Cache<String, TokenUserDetails> tokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(TokenAttributes.EXPIRES_IN_SECONDS_VALUE, TimeUnit.SECONDS)
                .build();
    }
}
