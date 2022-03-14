package net.wuxianjie.core.security;

public interface TokenService {

  TokenData getToken(String accountName, String accountRawPassword);

  TokenData refreshToken(String refreshToken);
}
