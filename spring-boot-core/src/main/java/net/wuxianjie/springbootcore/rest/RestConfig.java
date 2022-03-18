package net.wuxianjie.springbootcore.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * REST API 配置。
 *
 * @author 吴仙杰
 */
@Configuration
public class RestConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 当涉及 JSON 序列化时，设置默认编码为 UTF-8
        converters.stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .findFirst()
                .ifPresent(converter -> {
                    MappingJackson2HttpMessageConverter jsonConvert = (MappingJackson2HttpMessageConverter) converter;
                    jsonConvert.setDefaultCharset(StandardCharsets.UTF_8);
                });
    }
}
