package net.wuxianjie.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 用于区分在Spring IoC中注入的同类型Bean。主要用于两个地方：
 * <ul>
 *   <li>{@code @Bean("beanQualifier")}：定义指定名称的Bean</li>
 *   <li>{@code @Qualifier("beanQualifier")}：注入指定名称的Bean</li>
 * </ul>
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BeanQualifiers {

  /** 用于生成和验证JWT的签名密钥 */
  public static final String JWT_SIGNING_KEY = "jwtSingingKey";

  /** 任何人都可访问的请求路径，支持{@code AntPathRequestMatcher}模式，多个路径以英文逗号{@code ,}分隔 */
  public static final String ALLOWED_ANT_PATHS = "allowedPaths";
}
