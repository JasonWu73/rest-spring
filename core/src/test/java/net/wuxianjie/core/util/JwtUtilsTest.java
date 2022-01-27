package net.wuxianjie.core.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.constant.CommonValues;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 测试与使用 JWT
 *
 * @author 吴仙杰
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JwtUtilsTest {

  private static final String KEY_USERNAME = "user";
  private static final int EXPIRE_IN_SECONDS = 60;
  private static final String VAL_USERNAME = "吴仙杰";

  private static  String secretKey = "";
  private static String token = "";

  @Test
  @Order(1)
  void generateSecretKeyShouldNotReturnNull() {

    secretKey = JwtUtils.generateSingingKey();

    assertNotNull(secretKey);
    log.info("JWT 可用签名密钥：{}", secretKey);
  }

  @Test
  @Order(2)
  void generateTokenShouldNotReturnNull() {

    final Map<String, Object> claims = new HashMap<>() {{
      put(KEY_USERNAME, VAL_USERNAME);
    }};

    token = JwtUtils.generateToken(secretKey, claims, EXPIRE_IN_SECONDS);

    assertNotNull(token);
    log.info("生成的 Token 为：{}", token);
  }

  @Test
  @Order(3)
  void parseTokenShouldEqualsOriginalData() {

    final Map<String, Object> payload = JwtUtils.verifyAndParseToken(secretKey, token);

    final String username = (String) payload.get(KEY_USERNAME);

    assertEquals(username, VAL_USERNAME);

    final JSONObject json = JSONUtil.parseObj(payload);
    json.setDateFormat(CommonValues.DATE_TIME_FORMAT);
    log.info("Token 解析后的数据：{}", json);
  }
}