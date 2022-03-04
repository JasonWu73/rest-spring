package net.wuxianjie.core.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtUtilsTest {

    private static final String USERNAME_KEY = "user";
    private static final String USERNAME_VALUE = "吴仙杰";
    private static final int EXPIRE_IN_SECONDS = 60;

    private static String secretKey = "";
    private static String token = "";

    @Test
    @Order(1)
    void generateSecretKeyShouldNotReturnNull() {
        secretKey = JwtUtils.generateSingingKey();

        assertNotNull(secretKey);

        log.info("JWT 签名密钥：{}", secretKey);
    }

    @Test
    @Order(2)
    void generateTokenShouldNotReturnNull() {
        final Map<String, Object> claims = new HashMap<>() {{
            put(USERNAME_KEY, USERNAME_VALUE);
        }};

        token = JwtUtils.generateToken(secretKey, claims, EXPIRE_IN_SECONDS);

        assertNotNull(token);

        log.info("生成 JWT：{}", token);
    }

    @Test
    @Order(3)
    void parseTokenShouldEqualsOriginalData() {
        final Map<String, Object> payload = JwtUtils.verifyAndParseToken(secretKey, token);

        final String username = (String) payload.get(USERNAME_KEY);

        assertEquals(USERNAME_VALUE, username);

        final JSONObject jsonObject = JSONUtil.parseObj(payload);
        final String json = JSONUtil.toJsonStr(jsonObject, 2);

        log.info("解析 JWT：\n{}", json);
    }
}