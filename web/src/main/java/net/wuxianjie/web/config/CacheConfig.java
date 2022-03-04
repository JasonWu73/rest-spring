package net.wuxianjie.web.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import net.wuxianjie.core.dto.PrincipalDto;
import net.wuxianjie.web.constant.TokenAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    /**
     * 配置 Token 专用缓存
     */
    @Bean
    public Cache<String, PrincipalDto> tokenCache() {
        return Caffeine.newBuilder()
                // 设置在写入缓存的多长时间后自动清除
                .expireAfterWrite(TokenAttributes.EXPIRES_IN_SECONDS_VALUE, TimeUnit.SECONDS)
                .build();
    }
}
