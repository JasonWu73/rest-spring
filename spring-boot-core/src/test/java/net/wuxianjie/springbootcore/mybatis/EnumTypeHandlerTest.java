package net.wuxianjie.springbootcore.mybatis;

import org.apache.ibatis.binding.BindingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author 吴仙杰
 */
@MybatisTest
@ActiveProfiles("mybatis")
class EnumTypeHandlerTest {

  @Autowired
  private UserMapper underTest;

  @Test
  @DisplayName("插入枚举值")
  void canInsertEnum() {
    // given
    User user = new User();
    user.setUsername("测试用户");
    user.setEnabled(YesOrNo.YES);

    underTest.insertUser(user);

    // when
    YesOrNo actual = underTest.selectEnabledByUsername(user.getUsername());

    // then
    assertThat(actual).isEqualTo(user.getEnabled());
  }

  @Test
  @DisplayName("插入 null 枚举值，并返回枚举值")
  void canInsertNullEnumAndReturnEnum() {
    // given
    User user = new User();
    user.setUsername("测试用户");

    underTest.insertUser(user);

    // when
    YesOrNo actual = underTest.selectEnabledByUsername(user.getUsername());

    // then
    assertThat(actual).isEqualTo(YesOrNo.NO);
  }

  @Test
  @DisplayName("插入 null 枚举值，并返回原始类型对象值")
  void canInsertNullEnumAndReturnPrimitiveObj() {
    // given
    User user = new User();
    user.setUsername("测试用户");

    underTest.insertUser(user);

    // when
    Integer actual = underTest.selectEnabledByUsernameReturnIntObj(user.getUsername());

    // then
    assertThat(actual).isNull();
  }

  @Test
  @DisplayName("插入 null 枚举值，并返回原始类型值时会抛出异常")
  void canInsertNullEnumAndReturnPrimitiveWillThrowBindingException() {
    // given
    User user = new User();
    user.setUsername("测试用户");

    underTest.insertUser(user);

    // when
    // then
    assertThatThrownBy(() -> underTest.selectEnabledByUsernameReturnInt(user.getUsername()))
      .isInstanceOf(BindingException.class)
      .hasMessageContaining("attempted to return null from a method with a primitive return type (int)");
  }

  @Test
  @DisplayName("获取一个无法解析的枚举值")
  void canGetUnresolvedEnum() {
    // given
    // when
    YesOrNo actual = underTest.selectNegativeOne();

    // then
    assertThat(actual).isNull();
  }
}