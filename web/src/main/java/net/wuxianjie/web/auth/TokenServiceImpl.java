package net.wuxianjie.web.auth;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.rest.auth.TokenService;
import net.wuxianjie.core.rest.auth.dto.AuthConfigDto;
import net.wuxianjie.core.rest.auth.dto.PrincipalDto;
import net.wuxianjie.core.rest.auth.dto.TokenDto;
import net.wuxianjie.core.shared.exception.BadRequestException;
import net.wuxianjie.core.shared.exception.TokenAuthenticationException;
import net.wuxianjie.core.shared.util.JwtUtils;
import net.wuxianjie.web.user.UserDto;
import net.wuxianjie.web.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于本地缓存实现 Token 管理
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenServiceImpl implements TokenService {

    private final UserService userService;
    private final AuthConfigDto authConfig;
    private final PasswordEncoder passwordEncoder;
    private final Cache<String, PrincipalDto> tokenCache;

    @Override
    public TokenDto getToken(
            @NonNull final String accountName,
            @NonNull final String rawPassword
    ) {
        // 获取账号信息
        final UserDto account = userService.getUser(accountName);

        if (account == null) {
            throw new BadRequestException("账号不存在");
        }

        // 判断密码是否正确
        final String hashedPassword = account.getHashedPassword();
        final boolean isCorrect =
                passwordEncoder.matches(rawPassword, hashedPassword);

        if (!isCorrect) {
            throw new BadRequestException("密码错误");
        }

        // 构造需要缓存的账号信息
        final PrincipalDto principal = new PrincipalDto();
        principal.setAccountId(account.getUserId());
        principal.setAccountName(account.getUsername());
        principal.setRoles(account.getRoles());

        // 生成 Token，并写入缓存
        return generateAndCacheToken(principal);
    }

    @Override
    public TokenDto refreshToken(@NonNull final String refreshToken) {
        // 解析 Refresh Token
        final Map<String, Object> payload = JwtUtils.verifyAndParseToken(
                authConfig.getJwtSigningKey(),
                refreshToken
        );
        final String accountName = (String) payload.get(
                TokenAttributes.ACCOUNT_KEY
        );
        final String tokenType = (String) payload.get(TokenAttributes.TYPE_KEY);

        if (!tokenType.equals(TokenAttributes.TYPE_REFRESH_VALUE)) {
            throw new TokenAuthenticationException("Token 类型错误");
        }

        // 从缓存中查询账号信号
        final PrincipalDto principal = tokenCache.getIfPresent(accountName);

        // 校验 Refresh Token 是否有效
        if (principal == null ||
                !principal.getRefreshToken().equals(refreshToken)
        ) {
            throw new TokenAuthenticationException("Token 已过期");
        }

        // 获取最新的账号信息
        final UserDto account = userService.getUser(accountName);

        // 更新缓存的账号信息
        principal.setRoles(account.getRoles());

        // 生成 Token，并写入缓存
        return generateAndCacheToken(principal);
    }

    private TokenDto generateAndCacheToken(final PrincipalDto principal) {
        final Map<String, Object> jwtPayload = new HashMap<>();
        jwtPayload.put(TokenAttributes.ACCOUNT_KEY, principal.getAccountName());
        jwtPayload.put(TokenAttributes.ROLE_KEY, principal.getRoles());

        final String accessToken = generateToken(
                jwtPayload, TokenAttributes.TYPE_ACCESS_VALUE
        );
        final String refreshToken = generateToken(jwtPayload,
                TokenAttributes.TYPE_REFRESH_VALUE
        );

        // 完善账号信息
        principal.setAccessToken(accessToken);
        principal.setRefreshToken(refreshToken);

        // 写入缓存
        tokenCache.put(principal.getAccountName(), principal);

        return new TokenDto(
                TokenAttributes.EXPIRES_IN_SECONDS_VALUE,
                accessToken,
                refreshToken
        );
    }

    private String generateToken(
            final Map<String, Object> jwtPayload,
            final String tokenType
    ) {
        jwtPayload.put(TokenAttributes.TYPE_KEY, tokenType);

        return JwtUtils.generateToken(
                authConfig.getJwtSigningKey(),
                jwtPayload,
                TokenAttributes.EXPIRES_IN_SECONDS_VALUE
        );
    }
}
