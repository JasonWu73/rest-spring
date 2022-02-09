package net.wuxianjie.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC配置类
 *
 * @author 吴仙杰
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
    // 因为实现的是前后端分离项目，故设置后端全局返回类型为JSON
    configurer.defaultContentType(MediaType.APPLICATION_JSON);
  }
}
