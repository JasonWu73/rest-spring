package net.wuxianjie.core.rest;

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
import net.wuxianjie.core.shared.CommonValues;
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
 * 请求与响应数据处理配置。
 *
 * <ul>
 *     <li>配置自动去除 URL 及 Form 表单字符串参数的首尾空格</li>
 *     <li>配置自动去除 JSON 字符串参数的首尾空格</li>
 *     <li>配置 JSON 响应结果的序列化规则</li>
 * </ul>
 */
@Configuration
public class RequestResponseDataConfig {

  /**
   * 配置 JSON 序列化与反序列化行为。
   */
  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder -> {
      // 序列化后的字符串排除值为 null 的属性
      builder.serializationInclusion(JsonInclude.Include.NON_NULL);

      // 设置中国时区，默认使用 UTC 时间
      builder.timeZone(CommonValues.CHINA_TIME_ZONE);

      // 设置 Date 序列化后的日期字符串格式
      builder.serializers(new DateSerializer(false,
          new SimpleDateFormat(CommonValues.DATE_TIME_FORMAT)));

      // 设置 Java 8 LocalDate 序列化后的日期字符串格式
      builder.serializers(new LocalDateSerializer(
          DateTimeFormatter.ofPattern(CommonValues.DATE_FORMAT)));

      // 设置 Java 8 LocalDateTime 序列化后的日期时间字符串格式
      builder.serializers(new LocalDateTimeSerializer(
          DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT)));

      // 在序列化时去除字符串首尾空格
      builder.serializerByType(String.class, new JsonSerializer<String>() {

        @Override
        public void serialize(String value,
                              JsonGenerator gen,
                              SerializerProvider serializers)
            throws IOException {
          gen.writeString(StrUtil.trim(value));
        }
      });

      // 在反序列化时去除字符串首尾空格
      builder.deserializerByType(String.class,
          new StdScalarDeserializer<String>(String.class) {

            @Override
            public String deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
              return StrUtil.trim(p.getValueAsString());
            }
          });
    };
  }

  /**
   * 去除通过 URL 和 Form 表单提交的字符串参数的首尾空格。
   */
  @ControllerAdvice
  public static class ControllerStrParamTrimConfig {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
      final StringTrimmerEditor propertyEditor =
          new StringTrimmerEditor(false);

      binder.registerCustomEditor(String.class, propertyEditor);
    }
  }
}
