package net.wuxianjie.springbootcore.mybatis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

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
        final String username = "测试用户";
        final YesOrNo yes = YesOrNo.YES;
        final User user = new User();
        user.setUsername(username);
        user.setEnabled(yes);
        underTest.insertUser(user);

        // when
        final YesOrNo actual = underTest.selectEnabledByUsername(username);

        // then
        assertThat(actual).isEqualTo(yes);
    }

    @Test
    @DisplayName("插入枚举值 - 插入 null 值，并获取枚举值")
    void canInsertNullEnumReturnEnum() {
        // given
        final String username = "测试用户";
        final User user = new User();
        user.setUsername(username);
        underTest.insertUser(user);

        // when
        final YesOrNo actual = underTest.selectEnabledByUsername(username);

        // then
        assertThat(actual).isEqualTo(YesOrNo.NO);
    }

    @Test
    @DisplayName("插入枚举值 - 插入 null 值，但获取原始类型值")
    void canGetPrimitiveEnumValue() {
        // given
        final String username = "测试用户";
        final User user = new User();
        user.setUsername(username);
        underTest.insertUser(user);

        // when
        final Integer actual = underTest.selectEnabledByUsernameReturnInt(username);

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("无法获取一个无法解析的枚举值")
    void canNotGetUnresolvedEnumValue() {
        // given
        // when
        final YesOrNo actual = underTest.selectNegativeOne();

        // then
        assertThat(actual).isNull();
    }
}