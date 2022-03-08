package net.wuxianjie.core.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wuxianjie.core.exception.TokenAuthenticationException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    public static String createNewBase64SigningKey() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    public static String createNewJwt(String signingKey, Map<String, Object> payload, int expiresInSeconds) {
        SecretKey secretKey = createNewSecretKey(signingKey);

        DateTime expirationDateTime = DateUtil.offsetSecond(new Date(), expiresInSeconds);

        return Jwts.builder()
                .setClaims(payload)
                .setNotBefore(new Date())
                .setExpiration(expirationDateTime)
                .signWith(secretKey)
                .compact();
    }

    public static Map<String, Object> verifyTwtReturnPayload(String signingKey, String jwt) {
        try {
            SecretKey secretKey = createNewSecretKey(signingKey);

            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);

            Claims claims = jws.getBody();

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
        byte[] decodedSigningKey = Decoders.BASE64.decode(signingKey);
        return Keys.hmacShaKeyFor(decodedSigningKey);
    }

    private static Map<String, Object> toMap(Claims claims) {
        return new HashMap<>(claims);
    }
}
