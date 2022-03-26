package net.wuxianjie.springbootcore.rest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.shared.CommonValues;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 吴仙杰
 */
@JsonTest
@Import(JsonConfig.class)
class JsonConfigTest {

    private static final String JSON_VALUE_NO_WHITE_CHARACTER = "{" +
            "\"userId\":100," +
            "\"username\":\"吴仙杰\"," +
            "\"createTime\":\"2022-03-26 10:59:30\"," +
            "\"birthday\":\"2022-03-26\"," +
            "\"modifyTime\":\"2022-03-26 10:59:30\"," +
            "\"enabled\":1" +
            "}";

    private static final String JSON_VALUE_INCLUDE_WHITE_CHARACTER = "{" +
            "\"userId\":100," +
            "\"username\":\"\\t\\n吴仙杰 \"," +
            "\"createTime\":\"2022-03-26 10:59:30\"," +
            "\"birthday\":\"2022-03-26\"," +
            "\"modifyTime\":\"2022-03-26 10:59:30\"," +
            "\"enabled\":1" +
            "}";

    private static final String INVALID_DATE_STRING_JSON_VALUE = "{" +
            "\"modifyTime\":\"2022-03-26T10:59:30\"" +
            "}";

    @Autowired
    private JacksonTester<User> jacksonTester;

    @Test
    void jsonSerializeShouldEquals() throws IOException {
        User user = buildUser();

        assertEquals(JSON_VALUE_NO_WHITE_CHARACTER,
                jacksonTester.write(user).getJson()
        );
    }

    @Test
    void jsonDeserializeShouldEquals() throws IOException {
        User user = buildUser();
        User userFromJson = jacksonTester
                .parse(JSON_VALUE_INCLUDE_WHITE_CHARACTER)
                .getObject();

        assertAll("JSON 反序列化后的字段值应该相等",
                () -> assertEquals(userFromJson.userId, user.userId),
                () -> assertEquals(userFromJson.username, user.username.trim()),
                () -> assertEquals(userFromJson.password, user.password),
                () -> assertEquals(userFromJson.createTime, user.createTime),
                () -> assertEquals(userFromJson.birthday, user.birthday),
                () -> assertEquals(userFromJson.modifyTime, user.modifyTime),
                () -> assertEquals(userFromJson.enabled, user.enabled)
        );
    }

    @Test
    void whenInvalidFormatDateDeserializeShouldThrowException() {
        assertThrows(InvalidFormatException.class,
                () -> jacksonTester
                        .parse(INVALID_DATE_STRING_JSON_VALUE));
    }

    private User buildUser() {
        LocalDateTime createTime = LocalDateTime
                .parse("2022-03-26 10:59:30",
                        DateTimeFormatter
                                .ofPattern(CommonValues.DATE_TIME_FORMAT)
                );

        return new User(100, "\t\n吴仙杰 ", null,
                createTime, LocalDate.parse("2022-03-26"),
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