package net.wuxianjie.springbootcore.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.SecurityConfigProperties;

/**
 * 核心模块的配置项。
 *
 * @author 吴仙杰
 * @see SecurityConfigProperties
 * @see SecurityConfig
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreConfigData {

    /**
     * JWT 签名密钥。
     */
    private String jwtSigningKey;

    /**
     * 无需认证即可访问的请求路径，多个路径以英文逗号分隔，支持 AntPathMatcher 的匹配模式。
     */
    private String permitAllAntPatterns;
}
