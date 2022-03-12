package net.wuxianjie.core.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST API 配置。
 */
@Configuration
public class RestConfig implements WebMvcConfigurer {

  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> con) {
    // 当涉及 JSON 序列化时，设置默认编码为 UTF-8
    con.stream()
      .filter(MappingJackson2HttpMessageConverter.class::isInstance)
      .findFirst()
      .ifPresent(c -> {
        final MappingJackson2HttpMessageConverter converter =
          (MappingJackson2HttpMessageConverter) c;

        converter.setDefaultCharset(StandardCharsets.UTF_8);
      });
  }
}
