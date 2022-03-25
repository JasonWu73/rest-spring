package net.wuxianjie.springbootcore.mybatis;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * @author 吴仙杰
 */
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("mybatis")
@Slf4j
class UserTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void selectAllUsersShouldReturnFiveUsers() {
        List<User> userList = userMapper.selectAllUsers();

        Assertions.assertEquals(5, userList.size());

        userList.forEach(user -> log.info(user.toString()));
    }
}
