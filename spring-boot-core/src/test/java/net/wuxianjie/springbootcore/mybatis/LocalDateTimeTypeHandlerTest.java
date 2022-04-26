package net.wuxianjie.springbootcore.mybatis;

import cn.hutool.core.date.LocalDateTimeUtil;
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
    User user = new User();
    user.setUsername("测试用户");
    user.setCreateTime(LocalDateTime.now());

    underTest.insertUser(user);

    // when
    LocalDateTime actual = underTest.selectCreateTimeByUsername(user.getUsername());

    // then
    assertThat(actual).isEqualToIgnoringNanos(user.getCreateTime());
  }

  @Test
  @DisplayName("插入 LocalDate 值")
  void canInsertLocalDate() {
    // given
    User user = new User();
    user.setUsername("测试用户");
    user.setBirthday(LocalDate.now());

    underTest.insertUser(user);

    // when
    LocalDate actual = underTest.selectBirthdayByUsername(user.getUsername());

    // then
    assertThat(actual).isEqualTo(user.getBirthday());
  }

  @Test
  @DisplayName("插入 LocalDateTime null 值")
  void canInsertNullLocalDateTime() {
    // given
    User user = new User();
    user.setUsername("测试用户");

    underTest.insertUser(user);

    // when
    LocalDateTime actual = underTest.selectCreateTimeByUsername(user.getUsername());

    // then
    assertThat(actual).isNull();
  }

  @Test
  @DisplayName("插入 LocalDateTime 值，并返回字符串")
  void canInsertLocalDateTimeReturnStr() {
    // given
    User user = new User();
    user.setUsername("测试用户");
    user.setCreateTime(LocalDateTime.now());

    underTest.insertUser(user);

    // when
    String actual = underTest.selectCreateTimeStrByUsername(user.getUsername());

    // then
    String expected = LocalDateTimeUtil.formatNormal(user.getCreateTime());

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("插入 LocalDate 值，并返回字符串")
  void canInsertLocalDateReturnStr() {
    // given
    User user = new User();
    user.setUsername("测试用户");
    user.setBirthday(LocalDate.now());

    underTest.insertUser(user);

    // when
    String actual = underTest.selectBirthdayStrByUsername(user.getUsername());

    // then
    String expected = LocalDateTimeUtil.formatNormal(user.getBirthday());

    assertThat(actual).isEqualTo(expected);
  }
}