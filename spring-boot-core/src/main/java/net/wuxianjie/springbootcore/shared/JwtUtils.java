package net.wuxianjie.springbootcore.shared;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

  public static String createNewBase64SigningKey() {
    final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    return Encoders.BASE64.encode(secretKey.getEncoded());
  }

  public static String createNewJwt(String signingKey,
                                    Map<String, Object> payload,
                                    int expiresInSeconds) {
    final SecretKey secretKey = createNewSecretKey(signingKey);
    final DateTime expirationDateTime =
        DateUtil.offsetSecond(new Date(), expiresInSeconds);

    return Jwts.builder()
        .setClaims(payload)
        .setNotBefore(new Date())
        .setExpiration(expirationDateTime)
        .signWith(secretKey)
        .compact();
  }

  public static Map<String, Object> verifyTwtReturnPayload(String signingKey,
                                                           String jwt) {
    try {
      final SecretKey secretKey = createNewSecretKey(signingKey);

      final Jws<Claims> jws = Jwts.parserBuilder()
          .setSigningKey(secretKey)
          .build()
          .parseClaimsJws(jwt);

      final Claims claims = jws.getBody();

      return toMap(claims);
    } catch (MalformedJwtException e) {
      throw new TokenAuthenticationException("Token 格式错误", e);
    } catch (SignatureException e) {
      throw new TokenAuthenticationException("Token 签名错误", e);
    } catch (ExpiredJwtException e) {
      throw new TokenAuthenticationException("Token 已过期", e);
    }
  }

  private static SecretKey createNewSecretKey(String signingKey) {
    final byte[] decodedSigningKey = Decoders.BASE64.decode(signingKey);

    return Keys.hmacShaKeyFor(decodedSigningKey);
  }

  private static Map<String, Object> toMap(Claims claims) {
    return new HashMap<>(claims);
  }
}
