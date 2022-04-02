package net.wuxianjie.springbootcore.mybatis;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
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
    @DisplayName("插入 LocalDateTime 值")
    void canInsertLocalDateTime() {
        // given
        final String username = "测试用户";
        final LocalDateTime createTime = LocalDateTime.now();
        final User user = new User();
        user.setUsername(username);
        user.setCreateTime(createTime);
        underTest.insertUser(user);

        // when
        final LocalDateTime actual = underTest.selectCreateTimeByUsername(username);

        // then
        assertThat(actual).isEqualToIgnoringNanos(createTime);
    }

    @Test
    @DisplayName("插入 LocalDateTime 值 - 插入 null 值")
    void canInsertNullLocalDateTime() {
        // given
        final String username = "测试用户";
        final User user = new User();
        user.setUsername(username);
        underTest.insertUser(user);

        // when
        final LocalDateTime actual = underTest.selectCreateTimeByUsername(username);

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("插入 LocalDateTime 值 - 获取字符串")
    void canInsertLocalDateTimeReturnString() {
        // given
        final String username = "测试用户";
        final LocalDateTime createTime = LocalDateTime.now();
        final User user = new User();
        user.setUsername(username);
        user.setCreateTime(createTime);
        underTest.insertUser(user);

        // when
        final String actual = underTest.selectCreateTimeStringByUsername(username);

        // then
        final String expected = DateUtil.format(createTime, DatePattern.NORM_DATETIME_PATTERN);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("插入 LocalDate 值")
    void canInsertLocalDate() {
        // given
        final String username = "测试用户";
        final LocalDate birthday = LocalDate.now();
        final User user = new User();
        user.setUsername(username);
        user.setBirthday(birthday);
        underTest.insertUser(user);

        // when
        final LocalDate actual = underTest.selectBirthdayByUsername(username);

        // then
        assertThat(actual).isEqualTo(birthday);
    }
}