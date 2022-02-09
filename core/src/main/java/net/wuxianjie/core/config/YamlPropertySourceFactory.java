package net.wuxianjie.core.config;

import net.wuxianjie.core.exception.InternalServerException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Properties;

/**
 * 默认{@code @PropertySource}只能读取{@code .properties}文件，故需自己实现对YAML文件的读取
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/spring-yaml-propertysource">@PropertySource with YAML Files in Spring Boot | Baeldung</a>
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(final String name, final EncodedResource encodedResource) {

    // 使用`YamlPropertiesFactoryBean`将YAML格式的资源转换为`java.util.Properties`对象
    final YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    final Resource resource = encodedResource.getResource();

    factory.setResources(resource);

    final String filename = resource.getFilename();

    if (filename == null) {
      throw new InternalServerException("无法识别YAML配置文件的文件名");
    }

    final Properties properties = factory.getObject();

    if (properties == null) {
      throw new InternalServerException("识别YAML配置文件的FactoryBean无法完成初始化");
    }

    // 简单地返回一个`PropertyPropertySource`的新实例，它是一个包装器，允许`Spring`读取解析后的属性
    return new PropertiesPropertySource(filename, properties);
  }
}
