package net.wuxianjie.web.loginlog;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.util.JwtUtils;
import net.wuxianjie.springbootcore.util.NetUtils;
import net.wuxianjie.web.user.TokenAttributes;
import net.wuxianjie.web.user.UserDetails;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * 登录日志切面类。
 *
 * @author 吴仙杰
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoginLogAspect {

  private final SecurityConfig securityConfig;
  private final Cache<String, UserDetails> tokenCache;
  private final LoginLogService loginLogService;

  @Pointcut("execution(public net.wuxianjie.springbootcore.security.TokenData net.wuxianjie.springbootcore.security.TokenController.getToken(..))")
  public void login() {
  }

  /**
   * 记录登录日志。
   *
   * @param tokenData Token 身份验证通过后的返回结果
   */
  @AfterReturning(pointcut = "login()", returning = "tokenData")
  public void log(TokenData tokenData) {
    // 请求信息
    Optional<HttpServletRequest> reqOpt = NetUtils.getRequest();
    String reqIp = reqOpt.map(NetUtils::getRealIpAddress).orElse(null);
    String reqUri = reqOpt.map(HttpServletRequest::getRequestURI).orElse(null);

    // 用户信息
    String accessToken = tokenData.getAccessToken();
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), accessToken);
    String username = (String) payload.get(TokenAttributes.USERNAME_KEY);
    Integer userId = Optional.ofNullable(tokenCache.getIfPresent(username))
      .map(UserDetails::getAccountId)
      .orElse(null);

    // 打印到控制台
    log.info("用户（{}）成功登录系统，其请求 IP 为 {}，请求路径为 {}", username, reqIp, reqUri);

    // 登录日志入库
    LoginLog toSave = new LoginLog();
    toSave.setLoginTime(LocalDateTime.now());
    toSave.setUserId(userId);
    toSave.setUsername(username);
    toSave.setReqIp(reqIp);

    loginLogService.saveLoginLog(toSave);
  }
}
