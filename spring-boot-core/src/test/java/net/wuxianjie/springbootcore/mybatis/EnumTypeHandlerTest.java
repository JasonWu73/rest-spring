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
@ActiveProfiles("mybatis")
@MybatisTest
class EnumTypeHandlerTest {

    @Autowired
    private UserMapper underTest;

    @DisplayName("插入枚举字段值")
    @Test
    void itShouldCheckWhenInsertEnumField() {
        // given
        String username = "测试用户";
        YesOrNo yes = YesOrNo.YES;
        User user = new User(
                null,
                username,
                yes,
                null,
                null
        );

        underTest.insertUser(user);

        // when
        YesOrNo actual = underTest.selectEnabledByUsername(username);

        // then
        assertThat(actual).isEqualTo(yes);
    }

    @DisplayName("插入枚举字段 null 值")
    @Test
    void itShouldCheckWhenInsertNullEnumField() {
        // given
        String username = "测试用户";
        User user = new User(
                null,
                username,
                null,
                null,
                null
        );

        underTest.insertUser(user);

        // when
        YesOrNo actual = underTest.selectEnabledByUsername(username);

        // then
        assertThat(actual).isEqualTo(YesOrNo.NO);
    }
}