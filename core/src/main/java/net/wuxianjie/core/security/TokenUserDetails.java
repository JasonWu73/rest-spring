package net.wuxianjie.core.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUserDetails {

    private Integer accountId;
    private String accountName;
    private String accountRoles;
    private String accessToken;
    private String refreshToken;
}
