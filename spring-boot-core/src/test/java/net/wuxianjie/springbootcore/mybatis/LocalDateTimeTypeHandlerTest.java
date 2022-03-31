package net.wuxianjie.springbootcore.mybatis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
@MybatisTest
@ActiveProfiles("mybatis")
class LocalDateTimeTypeHandlerTest {

    @Autowired
    private UserMapper underTest;

    @Test
    @DisplayName("插入 LocalDateTime")
    void itShouldCheckWhenInsertLocalDateTime() {
        // given
        final String username = "测试用户";
        final LocalDateTime createTime = LocalDateTime.now();
        final User user = new User(null, username, null, createTime, null);
        underTest.insertUser(user);

        // when
        final LocalDateTime actual = underTest.selectCreateTimeByUsername(username);

        // then
        assertThat(actual).isEqualToIgnoringNanos(createTime);
    }

    @Test
    @DisplayName("插入 LocalDateTime null 值")
    void itShouldCheckWhenInsertLocalDateTimeNull() {
        // given
        final String username = "测试用户";
        final User user = new User(null, username, null, null, null);
        underTest.insertUser(user);

        // when
        final LocalDateTime actual = underTest.selectCreateTimeByUsername(username);

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("插入 LocalDate")
    void itShouldCheckWhenInsertLocalDate() {
        // given
        final String username = "测试用户";
        final LocalDate birthday = LocalDate.now();
        final User user = new User(null, username, null, null, birthday);
        underTest.insertUser(user);

        // when
        final LocalDate actual = underTest.selectBirthdayByUsername(username);

        // then
        assertThat(actual).isEqualTo(birthday);
    }

    @Test
    @DisplayName("插入 LocalDate null 值")
    void itShouldCheckWhenInsertNullLocalDate() {
        // given
        final String username = "测试用户";
        final User user = new User(null, username, null, null, null);
        underTest.insertUser(user);

        // when
        final LocalDate actual = underTest.selectBirthdayByUsername(username);

        // then
        assertThat(actual).isNull();
    }
}