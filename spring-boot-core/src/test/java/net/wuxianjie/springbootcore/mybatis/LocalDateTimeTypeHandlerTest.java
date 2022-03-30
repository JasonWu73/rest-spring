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
    @DisplayName("插入 LocalDateTime 字段值")
    void itShouldCheckWhenInsertLocalDateTimeField() {
        // given
        String username = "测试用户";
        LocalDateTime createTime = LocalDateTime.now();
        User user = new User(null, username, null, createTime, null);
        underTest.insertUser(user);

        // when
        LocalDateTime actual = underTest.selectCreateTimeByUsername(username);

        // then
        assertThat(actual).isEqualToIgnoringNanos(createTime);
    }

    @Test
    @DisplayName("插入 LocalDateTime 字段 null 值")
    void itShouldCheckWhenInsertNullLocalDateTimeField() {
        // given
        String username = "测试用户";
        User user = new User(null, username, null, null, null);
        underTest.insertUser(user);

        // when
        LocalDateTime actual = underTest.selectCreateTimeByUsername(username);

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("插入 LocalDate 字段值")
    void itShouldCheckWhenInsertLocalDateField() {
        // given
        String username = "测试用户";
        LocalDate birthday = LocalDate.now();
        User user = new User(null, username, null, null, birthday);
        underTest.insertUser(user);

        // when
        LocalDate actual = underTest.selectBirthdayByUsername(username);

        // then
        assertThat(actual).isEqualTo(birthday);
    }

    @Test
    @DisplayName("插入 LocalDate 字段 null 值")
    void itShouldCheckWhenInsertNullLocalDateField() {
        // given
        String username = "测试用户";
        User user = new User(null, username, null, null, null);
        underTest.insertUser(user);

        // when
        LocalDate actual = underTest.selectBirthdayByUsername(username);

        // then
        assertThat(actual).isNull();
    }
}