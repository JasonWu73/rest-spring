package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * JSON 反序列化（请求提交的 JSON 参数）以及 JSON 序列化（API 响应结果）配置类。
 *
 * @author 吴仙杰
 */
@Configuration
public class JsonConfig {

  /**
   * 配置 JSON 序列化与反序列化行为。
   *
   * @return {@link Jackson2ObjectMapperBuilderCustomizer}
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      // 序列化时排除值为 null 的字段
      builder.serializationInclusion(JsonInclude.Include.NON_NULL);

      // 设置与 JVM 相同的时区，默认使用 UTC 时间
      builder.timeZone(ZoneId.systemDefault().getId());

      // 设置 Date 序列化后的字符串格式
      SimpleDateFormat dateFormat = new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN);
      builder.serializers(new DateSerializer(false, dateFormat));

      // 设置 Java 8 LocalDate 序列化后的字符串格式
      builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));

      // 设置 Java 8 LocalDateTime 序列化后的字符串格式
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
      builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));

      // 在序列化时去除字符串值的首尾空白字符
      builder.serializerByType(String.class, new JsonSerializer<String>() {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
          gen.writeString(StrUtil.trim(value));
        }
      });

      // 在反序列化时去除字符串值的首尾空白字符
      builder.deserializerByType(String.class, new StdScalarDeserializer<String>(String.class) {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
          return StrUtil.trim(p.getValueAsString());
        }
      });

      // 设置 Java 8 LocalDateTime / LocalDate 反序列化
      builder.deserializerByType(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
          return LocalDateTime.parse(p.getValueAsString(), dateTimeFormatter);
        }
      });

      // 设置 Date 反序列化
      builder.deserializerByType(Date.class, new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
          String value = p.getValueAsString();

          try {
            return dateFormat.parse(value);
          } catch (ParseException e) {
            throw new InvalidFormatException(p, e.getMessage(), value, Date.class);
          }
        }
      });
    };
  }
}
