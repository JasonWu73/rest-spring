package net.wuxianjie.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Spring Web MVC配置
 *
 * @author 吴仙杰
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
    // JSON请求及返回默认使用UTF-8
    converters.stream()
      .filter(MappingJackson2HttpMessageConverter.class::isInstance)
      .findFirst()
      .ifPresent(converter ->
        ((MappingJackson2HttpMessageConverter) converter)
          .setDefaultCharset(StandardCharsets.UTF_8));
  }
}
