package net.wuxianjie.web.user;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@SpringBootTest(classes = TokenCacheConfig.class)
class TokenCacheConfigTest {

    @Autowired
    private Cache<String, UserDetails> tokenCache;

    @Test
    @DisplayName("配置 Token 缓存")
    void canGetTokenCache() {
        // given
        final String username = "测试用户";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountName(username);
        tokenCache.put(username, userDetails);

        // when
        final UserDetails actual = tokenCache.getIfPresent(username);

        // then
        assertThat(actual).isEqualTo(userDetails);
    }
}