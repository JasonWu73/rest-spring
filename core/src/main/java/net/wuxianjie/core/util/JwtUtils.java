package net.wuxianjie.core.util;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import net.wuxianjie.core.exception.TokenAuthenticationException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT (JSON Web TOKEN) 工具类
 *
 * @author 吴仙杰
 */
public class JwtUtils {

  /**
   * 生成 JWT 签名密钥
   *
   * @return JWT 签名密钥
   */
  public static String generateSingingKey() {

    final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    return Encoders.BASE64.encode(secretKey.getEncoded());
  }

  /**
   * 生成 Token
   *
   * @param singingKey JWT 签名密钥
   * @param payload JWT Payload
   * @param expiresInSeconds 多少秒后过期
   * @return JSON Web Token
   */
  public static String generateToken(@NonNull final String singingKey, @NonNull final Map<String, Object> payload, int expiresInSeconds) {

    return Jwts.builder()
        .setClaims(payload)
        .setNotBefore(new Date())
        .setExpiration(DateUtil.offsetSecond(new Date(), expiresInSeconds))
        .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(singingKey)))
      .compact();
  }

  /**
   * 验证并解析 Token
   *
   * @param secretKey JWT 签名密钥
   * @param jwt JSON Web Token
   * @return JWT payload
   * @throws TokenAuthenticationException 若 Token 验证失败
   */
  public static Map<String, Object> verifyAndParseToken(@NonNull final String secretKey, @NonNull final String jwt) throws TokenAuthenticationException {

    try {
      final Jws<Claims> jws = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .build()
        .parseClaimsJws(jwt);

      final Claims claims = jws.getBody();

      return toMap(claims);
    }
    catch (MalformedJwtException e) {
      final String message = String.format("Token 格式错误 - %s", e.getMessage());
      throw new TokenAuthenticationException(message, e);
    }
    catch (SignatureException e) {
      final String message = String.format("Token 签名错误 - %s", e.getMessage());
      throw new TokenAuthenticationException(message, e);
    }
    catch (ExpiredJwtException e) {
      final String message = String.format("Token 已过期 - %s", e.getMessage());
      throw new TokenAuthenticationException(message, e);
    }
  }

  private static Map<String, Object> toMap(final Claims claims) {
    return new HashMap<>(claims);
  }
}
