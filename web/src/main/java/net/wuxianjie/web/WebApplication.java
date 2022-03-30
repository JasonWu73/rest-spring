package net.wuxianjie.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 启动类。
 *
 * @author 吴仙杰
 */
@SpringBootApplication(scanBasePackages = "net.wuxianjie")
public class WebApplication {

    public static void main(final String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
