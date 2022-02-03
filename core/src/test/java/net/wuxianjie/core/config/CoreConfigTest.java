package net.wuxianjie.core.config;

import net.wuxianjie.core.constant.BeanQualifiers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 测试配置是否生效
 *
 * @author 吴仙杰
 */
@SpringBootTest(classes = CoreConfig.class)
class CoreConfigTest {

  @Autowired @Qualifier(BeanQualifiers.JWT_SIGNING_KEY) private String jwtSigningKey;
  @Autowired @Qualifier(BeanQualifiers.ALLOWED_ANT_PATHS) private String allowedAntPaths;

  @Test
  void whenFactoryProvidedThenYamlPropertiesInjected() {
    assertEquals(CoreProperties.JWT_SIGNING_KEY, jwtSigningKey);
    assertEquals(CoreProperties.ALLOWED_ANT_PATHS, allowedAntPaths);
  }
}