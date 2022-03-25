package net.wuxianjie.springbootcore.shared;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.Map;

import static cn.hutool.json.JSONUtil.toJsonStr;
import static net.wuxianjie.springbootcore.shared.JwtUtils.createJwt;
import static net.wuxianjie.springbootcore.shared.JwtUtils.createSigningKey;
import static net.wuxianjie.springbootcore.shared.JwtUtils.validateJwt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 测试 JWT 生成、验证等。
 *
 * @author 吴仙杰
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class JwtUtilsTest {

    private static final String USERNAME_KEY = "user";
    private static final String USERNAME_VALUE = "吴仙杰";
    private static final int EXPIRE_IN_SECONDS_VALUE = 60;

    private static String secretKey = "";
    private static String token = "";

    @Test
    @Order(1)
    void createSecretKeyShouldNotReturnNull() {
        secretKey = createSigningKey();

        assertNotNull(secretKey);

        log.info("JWT 签名密钥：{}", secretKey);
    }

    @Test
    @Order(2)
    void createTokenShouldNotReturnNull() {
        Map<String, Object> payload = new HashMap<>() {{
            put(USERNAME_KEY, USERNAME_VALUE);
        }};

        token = createJwt(secretKey, payload, EXPIRE_IN_SECONDS_VALUE);

        assertNotNull(token);

        log.info("生成 JWT：{}", token);
    }

    @Test
    @Order(3)
    void parseTokenShouldEqualsOriginalData() {
        Map<String, Object> payload = validateJwt(secretKey, token);

        String username = (String) payload.get(USERNAME_KEY);

        assertEquals(USERNAME_VALUE, username);

        log.info("解析 JWT：\n{}",
                toJsonStr(JSONUtil.parseObj(payload), 4)
        );
    }
}