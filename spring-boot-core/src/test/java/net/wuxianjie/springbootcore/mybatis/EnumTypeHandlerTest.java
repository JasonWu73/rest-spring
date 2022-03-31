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
    void itShouldCheckWhenInsertEnum() {
        // given
        final String username = "测试用户";
        final YesOrNo yes = YesOrNo.YES;
        final User user = new User(null, username, yes, null, null);
        underTest.insertUser(user);

        // when
        final YesOrNo actual = underTest.selectEnabledByUsername(username);

        // then
        assertThat(actual).isEqualTo(yes);
    }

    @Test
    @DisplayName("插入 null 值并获取枚举值")
    void itShouldCheckWhenInsertNullReturnEnum() {
        // given
        final String username = "测试用户";
        final User user = new User(null, username, null, null, null);
        underTest.insertUser(user);

        // when
        final YesOrNo actual = underTest.selectEnabledByUsername(username);

        // then
        assertThat(actual).isEqualTo(YesOrNo.NO);
    }

    @Test
    @DisplayName("获取一个无法映射的枚举值")
    void itShouldCheckWhenReturnCanNotResolvedEnum() {
        // given
        // when
        final YesOrNo actual = underTest.selectNegativeOne();

        // then
        assertThat(actual).isNull();
    }

    @Test
    @DisplayName("插入 null 值并获取原始类型值")
    void itShouldCheckWhenInsertNullReturnPrimitive() {
        // given
        final String username = "测试用户";
        final User user = new User(null, username, null, null, null);
        underTest.insertUser(user);

        // when
        final Integer actual = underTest.selectEnabledByUsernameReturnInt(username);

        // then
        assertThat(actual).isNull();
    }
}