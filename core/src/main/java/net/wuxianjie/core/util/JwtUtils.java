package net.wuxianjie.core.util;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.wuxianjie.core.exception.TokenAuthenticationException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT（JSON Web TOKEN）工具类
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

  /**
   * 生成JWT签名密钥
   *
   * @return JWT签名密钥
   */
  public static String generateSingingKey() {
    final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    return Encoders.BASE64.encode(secretKey.getEncoded());
  }

  /**
   * 生成Token
   *
   * @param singingKey JWT签名密钥
   * @param payload JWT Payload
   * @param expiresInSeconds 多少秒后过期
   * @return JSON Web Token
   */
  public static String generateToken(
      @NonNull final String singingKey,
      @NonNull final Map<String, Object> payload,
      int expiresInSeconds) {
    return Jwts.builder()
        .setClaims(payload)
        .setNotBefore(new Date())
        .setExpiration(DateUtil.offsetSecond(new Date(), expiresInSeconds))
        .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(singingKey)))
      .compact();
  }

  /**
   * 验证并解析Token
   *
   * @param secretKey JWT签名密钥
   * @param jwt JSON Web Token
   * @return JWT payload
   * @throws TokenAuthenticationException 若Token验证失败
   */
  public static Map<String, Object> verifyAndParseToken(@NonNull final String secretKey, @NonNull final String jwt)
      throws TokenAuthenticationException {
    try {
      final Jws<Claims> jws = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .build()
        .parseClaimsJws(jwt);

      final Claims claims = jws.getBody();

      return toMap(claims);
    }
    catch (MalformedJwtException e) {
      throw new TokenAuthenticationException("Token格式错误", e);
    }
    catch (SignatureException e) {
      throw new TokenAuthenticationException("Token签名错误", e);
    }
    catch (ExpiredJwtException e) {
      throw new TokenAuthenticationException("Token已过期", e);
    }
  }

  private static Map<String, Object> toMap(final Claims claims) {
    return new HashMap<>(claims);
  }
}
