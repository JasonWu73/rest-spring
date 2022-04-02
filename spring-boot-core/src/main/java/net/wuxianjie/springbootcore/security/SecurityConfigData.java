package net.wuxianjie.springbootcore.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 核心模块的 Web 安全相关配置项。
 *
 * @author 吴仙杰
 * @see SecurityConfigProperties
 * @see WebSecurityConfig
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
