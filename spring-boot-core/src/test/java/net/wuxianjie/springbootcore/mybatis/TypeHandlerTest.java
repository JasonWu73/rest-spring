package net.wuxianjie.springbootcore.mybatis;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 吴仙杰
 */
@SpringBootTest
@ActiveProfiles("mybatis")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TypeHandlerTest {

    private static final int DATA_ROWS_IN_DB = 5;
    private static final String NEW_USERNAME = "新增用户";

    @Autowired
    private UserMapper userMapper;

    @Test
    @Order(1)
    void insertOneUserShouldAddNewOneRow() {
        User user = new User();

        user.setCreateTime(LocalDateTime.now());
        user.setUsername(NEW_USERNAME);
        user.setBirthday(null);
        user.setEnabled(YesOrNo.resolve(1).orElseThrow());

        Assertions.assertEquals(1, userMapper.insertUser(user));
    }

    @Test
    @Order(2)
    void updateOneUserShouldUpdateOneRow() {
        User user = new User();

        user.setUsername(NEW_USERNAME);
        user.setEnabled(YesOrNo.resolve(2).orElse(YesOrNo.NO));

        Assertions.assertEquals(1, userMapper.updateUser(user));
    }

    @Test
    @Order(3)
    void deleteOneUserShouldDeleteOneRow() {
        Assertions.assertEquals(1, userMapper.deleteUserByName(NEW_USERNAME));
    }

    @Test
    void selectAllUsersShouldReturnAll() {
        List<User> userList = userMapper.selectAllUsers();

        Assertions.assertEquals(DATA_ROWS_IN_DB, userList.size());

        userList.forEach(System.out::println);
    }
}
