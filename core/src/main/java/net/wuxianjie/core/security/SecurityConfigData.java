package net.wuxianjie.core.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecurityConfigData {

    private String jwtSigningKey;

    private String permitAllAntPatterns;
}
