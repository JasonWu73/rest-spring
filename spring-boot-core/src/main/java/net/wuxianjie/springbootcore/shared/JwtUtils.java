package net.wuxianjie.springbootcore.shared;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
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
     *
     * @return Base64 字符串格式的 JWT 签名密钥
     */
    public static String createSigningKey() {
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        return Encoders.BASE64.encode(secretKey.getEncoded());
    }

    /**
     * 生成一个新的 JSON Web Token。
     *
     * @param signingKey       JWT 签名密钥
     * @param payload          JWT 中的有效载荷
     * @param expiresInSeconds JWT 的过期时间，单位秒
     * @return JWT
     */
    public static String createJwt(String signingKey,
                                   Map<String, Object> payload,
                                   int expiresInSeconds) {
        SecretKey secretKey = createSecretKey(signingKey);
        DateTime expirationDateTime = DateUtil.offsetSecond(new Date(), expiresInSeconds);

        return Jwts.builder()
                .setClaims(payload)
                .setNotBefore(new Date())
                .setExpiration(expirationDateTime)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 返回 JWT 的有效载荷（即解析结果）。
     *
     * @param signingKey JWT 签名密钥
     * @param jwt        JWT
     * @return JWT 中的有效载荷
     * @throws TokenAuthenticationException 若 JWT 校验不通过
     */
    public static Map<String, Object> validateJwt(String signingKey,
                                                  String jwt) throws TokenAuthenticationException {
        try {
            SecretKey secretKey = createSecretKey(signingKey);

            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt);

            Claims claims = jws.getBody();

            return new HashMap<>(claims);
        } catch (MalformedJwtException e) {
            throw new TokenAuthenticationException("Token 格式错误", e);
        } catch (SignatureException e) {
            throw new TokenAuthenticationException("Token 签名错误", e);
        } catch (ExpiredJwtException e) {
            throw new TokenAuthenticationException("Token 已过期", e);
        }
    }

    private static SecretKey createSecretKey(String signingKey) {
        byte[] decodedSigningKey = Decoders.BASE64.decode(signingKey);

        return Keys.hmacShaKeyFor(decodedSigningKey);
    }
}
