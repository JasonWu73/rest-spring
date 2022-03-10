package net.wuxianjie.core.shared;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.NonNull;

import java.util.Properties;

/**
 * 支持对 YAML 自定义配置文件（非 application.properties 和 application.yml）的读取。
 */
public class YamlSourceFactory implements PropertySourceFactory {

    @NonNull
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        final YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        final Resource resource = encodedResource.getResource();

        factory.setResources(resource);

        final String filename = resource.getFilename();

        if (filename == null) {
            throw new InternalServerException("无法识别 YAML 配置文件的文件名");
        }

        final Properties properties = factory.getObject();

        if (properties == null) {
            throw new InternalServerException("YAML 配置初始化失败");
        }

        return new PropertiesPropertySource(filename, properties);
    }
}
