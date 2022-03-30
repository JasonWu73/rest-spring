package net.wuxianjie.springbootcore.shared;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author 吴仙杰
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @DisplayName("生成 JWT 签名密钥")
    void itShouldGenerateNewJwtSigningKey() {
        // when
        secretKey = JwtUtils.createSigningKey();

        // then
        assertThat(secretKey).isNotNull();

        log.info("JWT 签名密钥：{}", secretKey);
    }

    @Test
    @Order(2)
    @DisplayName("生成 JWT")
    void itShouldGenerateNewJwt() {
        // given
        Map<String, Object> payload = new HashMap<>() {{
            put(USERNAME_KEY, USERNAME_VALUE);
        }};

        // when
        token = JwtUtils.createJwt(secretKey, payload, EXPIRE_IN_SECONDS_VALUE);

        // then
        assertThat(token).isNotNull();

        log.info("生成 JWT：{}", token);
    }

    @Test
    @Order(3)
    @DisplayName("验证并解析 JWT")
    void itShouldVerifyJwt() {
        // given
        // when
        Map<String, Object> payload = JwtUtils.verifyJwt(secretKey, token);
        String username = (String) payload.get(USERNAME_KEY);

        // then
        assertThat(username).isEqualTo(USERNAME_VALUE);

        log.info(
                "解析 JWT：\n{}",
                JSONUtil.toJsonStr(JSONUtil.parseObj(payload), 4)
        );
    }

    @Test
    @DisplayName("Token 格式错误")
    void willThrowExceptionWhenMalformedJwt() {
        // give
        String token = "token";

        // when
        // then
        assertThatThrownBy(() -> JwtUtils.verifyJwt(EXPIRED_JWT_SIGNING_KEY, token))
                .hasMessageContaining("Token 格式错误");
    }

    @Test
    @DisplayName("Token 签名密钥不匹配")
    void willThrowExceptionWhenSigningKeyChanged() {
        // give
        String signingKey = "qzW6sC+lngkBGVA1ZCikkOF3qbuvC7eT9RGMtKS8OCI=";

        // when
        // then
        assertThatThrownBy(() -> JwtUtils.verifyJwt(signingKey, EXPIRED_JWT))
                .hasMessageContaining("Token 签名密钥不匹配");
    }

    @Test
    @DisplayName("Token 已过期")
    void willThrowExceptionWhenExpiredJwt() {
        // give
        // when
        // then
        assertThatThrownBy(() -> JwtUtils.verifyJwt(EXPIRED_JWT_SIGNING_KEY, EXPIRED_JWT))
                .hasMessageContaining("Token 已过期");
    }
}