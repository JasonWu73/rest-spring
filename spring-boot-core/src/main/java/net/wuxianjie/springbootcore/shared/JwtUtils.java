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

/**
 * JSON Web Token 工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    /**
     * 生成一个新的 JWT 签名密钥（Base64 字符串格式）。
     */
    public static String createNewBase64SigningKey() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    /**
     * 生成一个新的 JWT。
     */
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

    /**
     * 返回 JWT 的有效载荷（即解析结果）；若 JWT 不合法，则抛出 {@link TokenAuthenticationException}。
     */
    public static Map<String, Object> validateJwtReturnPayload(String signingKey, String jwt) {
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

    private static Map<String, Object> toMap(Claims claims) {
        return new HashMap<>(claims);
    }

    private static SecretKey createNewSecretKey(String signingKey) {
        byte[] decodedSigningKey = Decoders.BASE64.decode(signingKey);

        return Keys.hmacShaKeyFor(decodedSigningKey);
    }
}
