package net.wuxianjie.core.dto;

import lombok.Data;
import net.wuxianjie.core.constant.Role;

/**
 * Token 认证后缓存的账号主体信息
 */
@Data
public class PrincipalDto {

    /**
     * 用于访问 API 接口的 Access Token
     */
    private String accessToken;

    /**
     * 只用于刷新 Access Token 的 Refresh Token
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
