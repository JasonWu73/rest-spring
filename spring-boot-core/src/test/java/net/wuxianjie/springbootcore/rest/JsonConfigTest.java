package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author 吴仙杰
 */
@JsonTest
@Import(JsonConfig.class)
class JsonConfigTest {

    @Autowired
    private JacksonTester<User> jacksonTester;

    @Test
    @DisplayName("JSON 序列化")
    void itShouldCheckJsonSerialize() throws IOException {
        // given
        final String json = "{" +
                "\"userId\":100," +
                "\"username\":\"吴仙杰\"," +
                "\"createTime\":\"2022-03-26 10:59:30\"," +
                "\"birthday\":\"2022-03-26\"," +
                "\"modifyTime\":\"2022-03-26 10:59:30\"," +
                "\"enabled\":1" +
                "}";
        final User user = buildUser();

        // when
        final String actual = jacksonTester.write(user).getJson();

        // then
        assertThat(actual).isEqualTo(json);
    }

    @Test
    @DisplayName("JSON 反序列化")
    void itShouldCheckJsonDeserialize() throws IOException {
        // given
        final String json = "{" +
                "\"userId\":100," +
                "\"username\":\"\\t\\n吴仙杰 \"," +
                "\"createTime\":\"2022-03-26 10:59:30\"," +
                "\"birthday\":\"2022-03-26\"," +
                "\"modifyTime\":\"2022-03-26 10:59:30\"," +
                "\"enabled\":1" +
                "}";
        final User user = buildUser();

        // when
        final User actual = jacksonTester.parse(json).getObject();

        // then
        user.setUsername(user.getUsername().trim());
        assertThat(actual).isEqualTo(user);
    }

    @Test
    @DisplayName("JSON 反序列化不符合格式要求的日期时间字符")
    void itShouldCheckMalformedDateTimeStrJsonDeserialize() {
        // given
        final String json = "{\"modifyTime\":\"2022-03-26T10:59:30\"}";

        // when
        // then
        assertThatThrownBy(() -> jacksonTester.parse(json))
                .isInstanceOf(InvalidFormatException.class);
    }

    private User buildUser() {
        final String dateStr = "2022-03-26 10:59:30";
        final LocalDateTime createTime = LocalDateTimeUtil.parse(dateStr, DatePattern.NORM_DATETIME_PATTERN);
        return new User(
                100,
                "\t\n吴仙杰 ",
                null,
                createTime,
                LocalDate.parse("2022-03-26"),
                Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant()),
                YesOrNo.YES
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {

        private Integer userId;
        private String username;
        private String password;
        private LocalDateTime createTime;
        private LocalDate birthday;
        private Date modifyTime;
        private YesOrNo enabled;
    }
}