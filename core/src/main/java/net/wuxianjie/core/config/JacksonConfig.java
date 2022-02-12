package net.wuxianjie.core.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import net.wuxianjie.core.constant.CommonValues;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * 定制的JSON序列化/反序列化行为，既会被应用于Spring MVC，又可作为{@code @Autowired}使用
 *
 * <p>Spring Boot的默认配置如下：</p>
 * <ul>
 *   <li>{@code MapperFeature.DEFAULT_VIEW_INCLUSION}</li>
 *   <li>{@code DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES}</li>
 *   <li>{@code SerializationFeature.WRITE_DATES_AS_TIMESTAMPS}</li>
 * </ul>
 *
 * <p>配置Spring MVC在接收请求参数时自动去除首尾空格</p>
 *
 * @author 吴仙杰
 * @see <a href="https://www.codeleading.com/article/39865996245/">SpringBoot去除字符串类型参数的前后空格 - 代码先锋网</a>
 */
@Configuration
public class JacksonConfig {

  /**
   * 定制JSON序列化行为
   *
   * @return 定制后的JSON序列化Bean
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      // 序列化后的字符串仅包含非null属性
      builder.serializationInclusion(JsonInclude.Include.NON_NULL);
      // 设置中国时区，非默认的UTC
      builder.timeZone(CommonValues.CHINA_TIME_ZONE);
      // 设置`Date`日期字符串格式
      builder.serializers(new DateSerializer(false, new SimpleDateFormat(CommonValues.DATE_TIME_FORMAT)));
      // 设置Java 8 `LocalDate`序列化后的日期字符串格式
      builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(CommonValues.DATE_FORMAT)));
      // 设置Java 8 `LocalDateTime`序列化后的日期时间字符串格式
      builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT)));

      // 在序列化时去除字符串首尾空格
      builder.serializerByType(String.class, new JsonSerializer<String>() {
        @Override
        public void serialize(final String value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
          gen.writeString(StrUtil.trim(value));
        }
      });

      // 在反序列化时去除字符串首尾空格
      builder.deserializerByType(String.class, new StdScalarDeserializer<String>(String.class) {
        @Override
        public String deserialize(final JsonParser p, final DeserializationContext ctx) throws IOException {
          // 去除前后空格
          return StrUtil.trim(p.getValueAsString());
        }
      });
    };
  }

  /**
   * 去除url或form表单中参数的首尾空格
   */
  @ControllerAdvice
  public static class ControllerStringParamTrimConfig {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
      // 创建String trim编辑器
      final StringTrimmerEditor propertyEditor = new StringTrimmerEditor(false);
      // 为String类对象注册编辑器
      binder.registerCustomEditor(String.class, propertyEditor);
    }
  }
}
