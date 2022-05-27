package net.wuxianjie.web.loginlog;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.security.SecurityPropertiesConfig;
import net.wuxianjie.springbootcore.security.TokenData;
import net.wuxianjie.springbootcore.util.JwtUtils;
import net.wuxianjie.springbootcore.util.NetUtils;
import net.wuxianjie.web.security.TokenAttributes;
import net.wuxianjie.web.user.CustomUserDetails;
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

  private final SecurityPropertiesConfig securityConfig;
  private final Cache<String, CustomUserDetails> tokenCache;
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
    Optional<HttpServletRequest> requestOptional = NetUtils.getRequest();
    String requestIp = requestOptional.map(NetUtils::getRealIpAddress).orElse(null);
    String requestUri = requestOptional.map(HttpServletRequest::getRequestURI).orElse(null);

    // 用户信息
    String accessToken = tokenData.getAccessToken();
    Map<String, Object> payload = JwtUtils.verifyJwt(securityConfig.getJwtSigningKey(), accessToken);
    String username = (String) payload.get(TokenAttributes.USERNAME_KEY);
    Integer userId = Optional.ofNullable(tokenCache.getIfPresent(username))
      .map(CustomUserDetails::getUserId)
      .orElse(null);

    // 打印到控制台
    log.info("登录系统，客户端信息：uri={};client={};user={}", requestUri, requestIp, username);

    // 保存登录日志数据
    LoginLog logToSave = new LoginLog();
    logToSave.setLoginTime(LocalDateTime.now());
    logToSave.setUserId(userId);
    logToSave.setUsername(username);
    logToSave.setRequestIp(requestIp);

    loginLogService.saveLoginLog(logToSave);
  }
}
