package net.wuxianjie.springbootcore.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现 Token 认证机制所需的配置数据。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityConfigData {

    /**
     * JWT 签名密钥。
     */
    private String jwtSigningKey;

    /**
     * 无需认证即可访问的请求路径，多个路径以英文逗号分隔，支持 AntPathMatcher 的匹配模式。
     */
    private String permitAllAntPatterns;
}
