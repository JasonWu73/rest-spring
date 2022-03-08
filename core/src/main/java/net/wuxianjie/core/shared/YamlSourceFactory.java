package net.wuxianjie.core.shared;

import net.wuxianjie.core.exception.InternalServerException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

public class YamlSourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        Resource resource = encodedResource.getResource();

        factory.setResources(resource);

        String filename = resource.getFilename();

        if (filename == null) {
            throw new InternalServerException("无法识别 YAML 配置文件的文件名");
        }

        Properties properties = factory.getObject();

        if (properties == null) {
            throw new InternalServerException("YAML 配置初始化失败");
        }

        return new PropertiesPropertySource(filename, properties);
    }
}
