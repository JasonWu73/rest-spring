package net.wuxianjie.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用于区分在 Spring IoC 中注入同类型 Bean 时的场景，如：
 *
 * <ul>
 *   <li>{@code @Bean("BeanQualifier.KEY")}：定义指定名称的 Bean</li>
 *   <li>{@code @Qualifier("BeanQualifier.KEY")}：注入指定名称的 Bean</li>
 * </ul>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanQualifiers {

    /**
     * 用于生成和验证 JWT 的签名密钥
     */
    public static final String JWT_SIGNING_KEY = "jwtSingingKey";

    /**
     * 开放访问的请求路径，
     * 支持 {@code AntPathRequestMatcher} 模式，
     * 多个路径以英文逗号（{@code ,}）分隔
     */
    public static final String ALLOWED_ANT_PATHS = "allowedPaths";
}
