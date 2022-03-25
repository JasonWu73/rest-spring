package net.wuxianjie.springbootcore.shared;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.Properties;

/**
 * 支持对 YAML 自定义配置文件的读取。
 * <p>
 * 默认 Spring Boot 仅支持读取 application.yml 配置文件。
 * </p>
 *
 * @author 吴仙杰
 */
public class YamlSourceFactory implements PropertySourceFactory {

    @NonNull
    @Override
    public PropertySource<?> createPropertySource(
            String name,
            EncodedResource resource
    ) throws InternalServerException {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();

        Resource heldResource = resource.getResource();

        factory.setResources(heldResource);

        String filename = Optional.ofNullable(heldResource.getFilename())
                .orElseThrow(() -> new InternalServerException(
                                "无法识别 YAML 配置文件的文件名"
                        )
                );

        Properties properties = Optional.ofNullable(factory.getObject())
                .orElseThrow(() -> new InternalServerException(
                                "YAML 配置文件读取失败"
                        )
                );

        return new PropertiesPropertySource(filename, properties);
    }
}
