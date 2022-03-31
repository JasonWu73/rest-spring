package net.wuxianjie.springbootcore.shared.util;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.shared.exception.TokenAuthenticationException;

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
        return Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    }

    /**
     * 生成一个新的 JSON Web Token。
     *
     * @param signingKey       JWT 签名密钥
     * @param payload          JWT 中的有效载荷
     * @param expiresInSeconds JWT 的过期时间，单位秒
     * @return JWT
     */
    public static String createJwt(final String signingKey,
                                   final Map<String, Object> payload,
                                   final int expiresInSeconds) {
        return Jwts.builder()
                .setClaims(payload)
                .setNotBefore(new Date())
                .setExpiration(DateUtil.offsetSecond(new Date(), expiresInSeconds))
                .signWith(createSecretKey(signingKey))
                .compact();
    }

    /**
     * 验证 JWT 有效期，并返回 JWT 的有效载荷（即解析结果）。
     *
     * @param signingKey JWT 签名密钥
     * @param jwt        JWT
     * @return JWT 中的有效载荷
     * @throws TokenAuthenticationException 若 JWT 校验不通过
     */
    public static Map<String, Object> verifyJwt(final String signingKey,
                                                final String jwt) throws TokenAuthenticationException {
        try {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(createSecretKey(signingKey))
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            return new HashMap<>(claims);
        } catch (MalformedJwtException e) {
            throw new TokenAuthenticationException("Token 格式错误", e);
        } catch (SignatureException e) {
            throw new TokenAuthenticationException("Token 签名密钥不匹配", e);
        } catch (ExpiredJwtException e) {
            throw new TokenAuthenticationException("Token 已过期", e);
        }
    }

    private static SecretKey createSecretKey(final String signingKey) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(signingKey));
    }
}
