package net.wuxianjie.springbootcore.security;

import net.wuxianjie.springbootcore.shared.YamlSourceFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author 吴仙杰
 */
@SpringBootApplication
@PropertySource(value = "classpath:security.yml", factory = YamlSourceFactory.class)
class TestApplication {

    static void main(final String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
