package net.wuxianjie.web.oplog;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.exception.InternalException;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.util.NetUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 操作日志切面类。
 *
 * @author 吴仙杰
 * @see OpLogger
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OpLogAspect {

  private static final String VOID_RETURN_TYPE = "void";

  private final ObjectMapper objectMapper;
  private final OpLogService logService;

  /**
   * 对标有 {@link OpLogger} 注解的方法记录操作日志。
   *
   * @param joinPoint {@link JoinPoint}
   * @param returnObj 方法返回值
   * @throws JsonProcessingException 当对入参或返回值执行 JSON 序列化出错时抛出
   */
  @AfterReturning(pointcut = "@annotation(net.wuxianjie.web.oplog.OpLogger)", returning = "returnObj")
  public void log(JoinPoint joinPoint, Object returnObj) throws JsonProcessingException {
    // 请求信息
    Optional<HttpServletRequest> reqOpt = NetUtils.getRequest();
    String reqIp = reqOpt.map(NetUtils::getRealIpAddress).orElse(null);
    String reqUri = reqOpt.map(HttpServletRequest::getRequestURI).orElse(null);

    // 用户信息
    Optional<TokenUserDetails> userOpt = AuthUtils.getCurrentUser();
    Integer userId = userOpt.map(TokenUserDetails::getAccountId).orElse(null);
    String username = userOpt.map(TokenUserDetails::getAccountName).orElse(null);

    // 方法信息
    String methodMsg = getMethodMsg(joinPoint);
    String qualifiedMethodName = getQualifiedMethodName(joinPoint);
    Map<String, Object> params = getParams(joinPoint);
    String paramJson = objectMapper.writeValueAsString(params);
    String returnJson = isVoidReturnType(joinPoint) ? VOID_RETURN_TYPE : objectMapper.writeValueAsString(returnObj);

    // 打印到控制台
    log.info("用户（{}）{}，其请求 IP 为 {}，请求路径为 {}，具体执行方法为 {}，入参为 {}，返回值为 {}",
      username, methodMsg, reqIp, reqUri, qualifiedMethodName, paramJson, returnJson);

    // 操作日志入库
    OpLog toSave = new OpLog();
    toSave.setOpTime(LocalDateTime.now());
    toSave.setUserId(userId);
    toSave.setUsername(username);
    toSave.setReqIp(reqIp);
    toSave.setReqUri(reqUri);
    toSave.setMethodName(qualifiedMethodName);
    toSave.setMethodMsg(methodMsg);
    toSave.setParamJson(paramJson);
    toSave.setReturnJson(returnJson);

    logService.saveOpLog(toSave);
  }

  private Method getMethod(JoinPoint joinPoint) {
    return getSignature(joinPoint).getMethod();
  }

  private MethodSignature getSignature(JoinPoint joinPoint) {
    return (MethodSignature) joinPoint.getSignature();
  }

  private String getMethodMsg(JoinPoint joinPoint) {
    Method method = getMethod(joinPoint);
    return Optional.ofNullable(method.getAnnotation(OpLogger.class))
      .map(OpLogger::value)
      .orElseThrow(() -> new InternalException("未找到 @" + OpLogger.class.getSimpleName() + "注解"));
  }

  private String getQualifiedMethodName(JoinPoint joinPoint) {
    Method method = getMethod(joinPoint);
    String methodName = method.getName();
    Class<?> clazz = joinPoint.getTarget().getClass();
    String className = clazz.getName();
    return className + "." + methodName;
  }

  private Map<String, Object> getParams(JoinPoint joinPoint) {
    MethodSignature methodSignature = getSignature(joinPoint);
    return Optional.ofNullable(joinPoint.getArgs())
      .map(args -> {
        Object[] values = Arrays.stream(args)
          .map(arg -> {
            if (arg instanceof MultipartFile) {
              return ((MultipartFile) arg).getOriginalFilename();
            }

            return arg;
          })
          .toArray();
        String[] keys = methodSignature.getParameterNames();
        return ArrayUtil.zip(keys, values, true);
      })
      .orElse(new HashMap<>());
  }

  private boolean isVoidReturnType(JoinPoint joinPoint) {
    MethodSignature methodSignature = getSignature(joinPoint);
    String returnType = methodSignature.getReturnType().toString();
    return StrUtil.equalsIgnoreCase(returnType, VOID_RETURN_TYPE);
  }
}
