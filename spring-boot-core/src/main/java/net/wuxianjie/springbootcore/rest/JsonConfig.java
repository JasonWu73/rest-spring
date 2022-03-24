package net.wuxianjie.springbootcore.rest;

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
import net.wuxianjie.springbootcore.shared.CommonValues;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * JSON 反序列化（请求提交的 JSON 参数）以及 JSON 序列化（API 响应结果）配置。
 *
 * @author 吴仙杰
 */
@Configuration
public class JsonConfig {

    /**
     * 配置 JSON 序列化与反序列化行为。
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            // 序列化时排除值为 null 的字段
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);

            // 设置中国时区，默认使用 UTC 时间
            builder.timeZone(CommonValues.CHINA_TIME_ZONE);

            // 设置 Date 序列化后的字符串格式
            builder.serializers(new DateSerializer(false,
                            new SimpleDateFormat(CommonValues.DATE_TIME_FORMAT)
                    )
            );

            // 设置 Java 8 LocalDate 序列化后的字符串格式
            builder.serializers(new LocalDateSerializer(
                            DateTimeFormatter
                                    .ofPattern(CommonValues.DATE_FORMAT)
                    )
            );

            // 设置 Java 8 LocalDateTime 序列化后的字符串格式
            builder.serializers(new LocalDateTimeSerializer(
                            DateTimeFormatter
                                    .ofPattern(CommonValues.DATE_TIME_FORMAT)
                    )
            );

            // 在序列化时去除字符串值的首尾空格
            builder.serializerByType(String.class,
                    new JsonSerializer<String>() {

                        @Override
                        public void serialize(
                                String value,
                                JsonGenerator gen,
                                SerializerProvider serializers
                        ) throws IOException {
                            gen.writeString(StrUtil.trim(value));
                        }
                    }
            );

            // 在反序列化时去除字符串值的首尾空格
            builder.deserializerByType(String.class,
                    new StdScalarDeserializer<String>(String.class) {

                        @Override
                        public String deserialize(
                                JsonParser p,
                                DeserializationContext ctxt
                        ) throws IOException {
                            return StrUtil.trim(p.getValueAsString());
                        }
                    }
            );
        };
    }
}
