package net.wuxianjie.core.rest.auth.dto;

import lombok.Data;
import net.wuxianjie.core.rest.auth.Role;

/**
 * Token 认证后缓存的账号主体信息
 */
@Data
public class PrincipalDto {

    /**
     * 用于 API 访问鉴权的 Access Token
     */
    private String accessToken;

    /**
     * 只用于刷新的 Refresh Token
     */
    private String refreshToken;

    /**
     * 该 Token 对应的账号 ID
     */
    private Integer accountId;

    /**
     * 该 Token 对应的账号名称
     */
    private String accountName;

    /**
     * 账号角色，以英文逗号（{@code ,}）分隔，
     * 全部为小写字母，且不包含 {@code ROLE_} 前缀。
     *
     * <p>详见：{@link Role#value()}</p>
     */
    private String roles;
}
