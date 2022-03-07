package net.wuxianjie.core.shared;

import net.wuxianjie.core.shared.exception.InternalServerException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

/**
 * 默认 {@code @PropertySource} 只能读取 {@code .properties} 文件，
 * 故需自己实现对 YAML 文件的读取
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(
            final String name,
            final EncodedResource encodedResource
    ) {
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
