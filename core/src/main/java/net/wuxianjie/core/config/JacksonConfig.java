package net.wuxianjie.core.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import net.wuxianjie.core.constant.CommonValues;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * 定制Spring Boot的JSON序列化（处理发送至客户端的响应）与反序列化（处理发送至服务端的请求）
 *
 * <p>Spring Boot的默认配置如下：</p>
 * <ul>
 *   <li>{@code MapperFeature.DEFAULT_VIEW_INCLUSION}</li>
 *   <li>{@code DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES}</li>
 *   <li>{@code SerializationFeature.WRITE_DATES_AS_TIMESTAMPS}</li>
 * </ul>
 *
 * @author 吴仙杰
 * @see <a href="https://www.baeldung.com/spring-boot-customize-jackson-objectmapper">Spring Boot: Customize the Jackson ObjectMapper | Baeldung</a>
 */
@Configuration
public class JacksonConfig {

  /**
   * 定制默认的JSON序列化
   *
   * <p>{@code ObjectMapper}默认由{@code Jackson2ObjectMapperBuilder}创建</p>
   *
   * @return 定制后的JSON序列化Bean
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      // 序列化后的字符串仅包含非null属性
      builder.serializationInclusion(Include.NON_NULL);
      // 设置中国时区，非默认的UTC
      builder.timeZone(CommonValues.CHINA_TIME_ZONE);
      // 设置`Date`日期字符串格式
      builder.simpleDateFormat(CommonValues.DATE_TIME_FORMAT);
      // 设置Java 8 `LocalDate`序列化后的日期字符串格式
      builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(CommonValues.DATE_FORMAT)));
      // 设置Java 8 `LocalDateTime`序列化后的日期时间字符串格式
      builder.serializers(new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT)));
    };
  }

  /**
   * 用于项目中通过Spring依赖注入的预配置JSON序列化/反序列化工具Bean
   *
   * <p>注意：因未使用{@code @Primary}, 故该Bean并没有替代默认{@code ObjectMapper}，仅仅作为工具Bean使用</p>
   *
   * @return 预配置的JSON序列化/反序列化工具Bean
   */
  @Bean
  public ObjectMapper objectMapper() {
    final ObjectMapper mapper = new ObjectMapper();

    // 定制 JSON 序列化/反序列化
    // 属性命名转换仅对POJO有效，对`java.util.Map`是无效的
    //mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    // 配置在反序列化遇到未知属性时并不报错
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    // 配置序列化结果仅包含非null属性
    mapper.setSerializationInclusion(Include.NON_NULL);
    // 配置中国时区
    mapper.setTimeZone(TimeZone.getTimeZone(CommonValues.CHINA_TIME_ZONE));
    // 配置`Date`日期字符串格式
    mapper.setDateFormat(new SimpleDateFormat(CommonValues.DATE_TIME_FORMAT));

    // 配置Java 8 `LocalDate`与`LocalDateTime`的序列化字符串格式
    final JavaTimeModule timeModule = new JavaTimeModule();

    timeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(CommonValues.DATE_FORMAT)));
    timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT)));

    mapper.registerModule(timeModule);

    return mapper;
  }
}
