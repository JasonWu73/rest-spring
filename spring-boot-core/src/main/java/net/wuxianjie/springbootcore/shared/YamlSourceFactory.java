package net.wuxianjie.springbootcore.shared;

import net.wuxianjie.springbootcore.exception.InternalException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Optional;
import java.util.Properties;

/**
 * 支持对 YAML 自定义配置文件的读取。
 *
 * <p>
 * 默认 Spring Boot 仅支持读取 application.yml 配置文件。
 * </p>
 *
 * @author 吴仙杰
 */
public class YamlSourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws InternalException {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    Resource heldResource = resource.getResource();
    factory.setResources(heldResource);

    String filename = Optional.ofNullable(heldResource.getFilename())
      .orElseThrow(() -> new InternalException("无法识别 YAML 配置文件的文件名"));

    Properties properties = Optional.ofNullable(factory.getObject())
      .orElseThrow(() -> new InternalException("无法读取 YAML 配置文件内容"));

    return new PropertiesPropertySource(filename, properties);
  }
}
