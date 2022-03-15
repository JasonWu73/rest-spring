package net.wuxianjie.springbootcore.shared;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.NonNull;

import java.util.Properties;

/**
 * 支持对 YAML 自定义配置文件的读取。
 *
 * <p>默认 Spring 仅支持读取 application.yml 的 YAML 内置配置文件。</p>
 */
public class YamlSourceFactory implements PropertySourceFactory {

  @NonNull
  @Override
  public PropertySource<?> createPropertySource(String name,
                                                EncodedResource resource)
      throws InternalServerException {
    final YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    final Resource heldResource = resource.getResource();

    factory.setResources(heldResource);

    final String filename = heldResource.getFilename();

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
