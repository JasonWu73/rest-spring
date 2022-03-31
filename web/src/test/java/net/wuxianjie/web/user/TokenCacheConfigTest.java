package net.wuxianjie.web.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
class TokenCacheConfigTest {

    private ApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp() {
        contextRunner = new ApplicationContextRunner()
                .withUserConfiguration(TokenCacheConfig.class);
    }

    @Test
    @DisplayName("已配置 Token 缓存")
    void tokenCache() {
        // given
        // when
        // then
        contextRunner.run(context -> assertThat(context).hasSingleBean(TokenCacheConfig.class));
    }
}