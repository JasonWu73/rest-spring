package net.wuxianjie.springbootcore.shared;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
        Map<String, Object> payload = new HashMap<>() {
            {
                put(USERNAME_KEY, USERNAME_VALUE);
            }
        };

        // when
        token = JwtUtils.createJwt(secretKey, payload, EXPIRE_IN_SECONDS_VALUE);

        // then
        assertThat(token).isNotNull();

        log.info("生成 JWT：{}", token);
    }

    @Test
    @Order(3)
    @DisplayName("校验并解析 JWT")
    void itShouldValidateJwt() {
        // when
        Map<String, Object> payload = JwtUtils.validateJwt(secretKey, token);
        String username = (String) payload.get(USERNAME_KEY);

        // then
        assertThat(username).isEqualTo(USERNAME_VALUE);

        log.info(
                "解析 JWT：\n{}",
                JSONUtil.toJsonStr(JSONUtil.parseObj(payload), 4)
        );
    }

    @Test
    @DisplayName("当 Token 格式错误")
    void willThrowExceptionWhenMalformedJwt() {
        assertThatExceptionOfType(TokenAuthenticationException.class)
                .isThrownBy(() ->
                        JwtUtils.validateJwt(
                                EXPIRED_JWT_SIGNING_KEY,
                                "token"
                        )
                )
                .withMessageContaining("Token 格式错误");
    }

    @Test
    @DisplayName("当 Token 签名密钥改变")
    void willThrowExceptionWhenSigningKeyChanged() {
        assertThatExceptionOfType(TokenAuthenticationException.class)
                .isThrownBy(() ->
                        JwtUtils.validateJwt(
                                "qzW6sC+lngkBGVA1ZCikkOF3qbuvC7eT9RGMtKS8OCI=",
                                EXPIRED_JWT
                        )
                )
                .withMessageContaining("Token 签名不匹配");
    }

    @Test
    @DisplayName("当 Token 已过期")
    void willThrowExceptionWhenExpiredJwt() {
        assertThatExceptionOfType(TokenAuthenticationException.class)
                .isThrownBy(() ->
                        JwtUtils.validateJwt(
                                EXPIRED_JWT_SIGNING_KEY,
                                EXPIRED_JWT
                        )
                )
                .withMessageContaining("Token 已过期");
    }
}