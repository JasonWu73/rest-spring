package net.wuxianjie.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试配置是否生效
 *
 * @author 吴仙杰
 */
@SpringBootTest(classes = CoreConfig.class)
class CoreConfigTest {

  private static final String SECRET_KEY = "6/ATA2JKtDzT0jhs+loVxzaGiwROIn4bThvdhAIn5wo=";
  private static final String ALLOWED_ANT_PATHS = "/test-1, /test-2";

  @Autowired private String jwtSecretKey;
  @Autowired private String allowedAntPaths;

  @Test
  void whenFactoryProvidedThenYamlPropertiesInjected() {
    assertEquals(SECRET_KEY, jwtSecretKey);
    assertEquals(ALLOWED_ANT_PATHS, allowedAntPaths);
  }
}