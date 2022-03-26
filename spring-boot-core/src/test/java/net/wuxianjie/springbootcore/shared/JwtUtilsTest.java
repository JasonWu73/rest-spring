package net.wuxianjie.springbootcore.shared;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 吴仙杰
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class JwtUtilsTest {

    private static final String USERNAME_KEY = "user";
    private static final String USERNAME_VALUE = "吴仙杰";
    private static final int EXPIRE_IN_SECONDS_VALUE = 60;

    private static final String EXPIRED_JWT_SIGNING_KEY = "2t8uwvcI4Mw+jroZNzAcUen59renhGWugL/dtW1QBfA=";
    private static final String EXPIRED_JWT = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoi5ZC05LuZ5p2wIiwibmJmIjoxNjQ4MjI2MDEwLCJleHAiOjE2NDgyMjYwNzB9.giKO4eACSISzTUX6y9dMdAe8hSWJ-Zc-YR6Snwdy2tY";

    private static String secretKey = "";
    private static String token = "";

    @Test
    @Order(1)
    void createSecretKeyShouldNotReturnNull() {
        secretKey = JwtUtils.createSigningKey();

        Assertions.assertNotNull(secretKey);

        log.info("JWT 签名密钥：{}", secretKey);
    }

    @Test
    @Order(2)
    void createTokenShouldNotReturnNull() {
        Map<String, Object> payload = new HashMap<>() {{
            put(USERNAME_KEY, USERNAME_VALUE);
        }};

        token = JwtUtils.createJwt(secretKey, payload, EXPIRE_IN_SECONDS_VALUE);

        Assertions.assertNotNull(token);

        log.info("生成 JWT：{}", token);
    }

    @Test
    @Order(3)
    void parseTokenShouldEqualsOriginalData() {
        Map<String, Object> payload = JwtUtils.validateJwt(secretKey, token);

        String username = (String) payload.get(USERNAME_KEY);

        Assertions.assertEquals(USERNAME_VALUE, username);

        log.info("解析 JWT：\n{}",
                JSONUtil.toJsonStr(JSONUtil.parseObj(payload), 4)
        );
    }

    @Test
    void whenMalformedJwtShouldThrowException() {
        TokenAuthenticationException thrown = Assertions.assertThrows(
                TokenAuthenticationException.class,
                () -> JwtUtils.validateJwt(EXPIRED_JWT_SIGNING_KEY, "JSON Web Token")
        );

        Assertions.assertTrue(thrown.getMessage().contains("Token 格式错误"));
    }

    @Test
    void whenWrongSignatureJwtShouldThrowException() {
        TokenAuthenticationException thrown = Assertions.assertThrows(
                TokenAuthenticationException.class,
                () -> JwtUtils.validateJwt(
                        "qzW6sC+lngkBGVA1ZCikkOF3qbuvC7eT9RGMtKS8OCI=",
                        EXPIRED_JWT
                )
        );

        Assertions.assertTrue(thrown.getMessage().contains("Token 签名错误"));
    }

    @Test
    void whenExpiredJwtShouldThrowException() {
        TokenAuthenticationException thrown = Assertions.assertThrows(
                TokenAuthenticationException.class,
                () -> JwtUtils.validateJwt(EXPIRED_JWT_SIGNING_KEY, EXPIRED_JWT)
        );

        Assertions.assertTrue(thrown.getMessage().contains("Token 已过期"));
    }
}