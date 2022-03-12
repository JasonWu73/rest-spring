package net.wuxianjie.core.security;

import org.springframework.lang.NonNull;

public interface TokenService {

  @NonNull
  TokenData getToken(String accountName, String accountRawPassword);

  @NonNull
  TokenData refreshToken(String refreshToken);
}
