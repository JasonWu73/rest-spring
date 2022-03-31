package net.wuxianjie.springbootcore.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class UserDetails implements TokenUserDetails {

    private Integer accountId;

    private String accountName;

    private String roles;

    private String accessToken;

    private String refreshToken;
}
