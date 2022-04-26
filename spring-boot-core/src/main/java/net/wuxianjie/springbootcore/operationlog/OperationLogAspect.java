package net.wuxianjie.springbootcore.operationlog;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.exception.InternalException;
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
 * 记录操作日志。
 *
 * @author 吴仙杰
 * @see OperationLogger
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

  static final String VOID_RETURN_TYPE = "void";

  private final ObjectMapper objectMapper;
  private final OperationLogService logService;

  /**
   * 对标有 {@link OperationLogger} 注解的方法记录操作日志。
   *
   * @param joinPoint {@link JoinPoint}
   * @param returnObj 方法返回值
   * @throws JsonProcessingException 当对入参或返回值执行 JSON 序列化出错时抛出
   */
  @AfterReturning(pointcut = "@annotation(OperationLogger)", returning = "returnObj")
  public void log(JoinPoint joinPoint, Object returnObj) throws JsonProcessingException {
    // 请求信息
    Optional<HttpServletRequest> reqOpt = NetUtils.getRequest();
    String reqIp = reqOpt.map(NetUtils::getRealIpAddress).orElse(null);
    String reqUri = reqOpt.map(HttpServletRequest::getRequestURI).orElse(null);

    // 用户信息
    Optional<TokenUserDetails> userOpt = AuthUtils.getCurrentUser();
    Integer operatorId = userOpt.map(TokenUserDetails::getAccountId).orElse(null);
    String operatorName = userOpt.map(TokenUserDetails::getAccountName).orElse(null);

    // 方法信息
    String methodMessage = getMethodMessage(joinPoint);
    String qualifiedMethodName = getQualifiedMethodName(joinPoint);
    Map<String, Object> params = getParameters(joinPoint);
    String paramJson = objectMapper.writeValueAsString(params);
    String returnJson = isVoidReturnType(joinPoint) ? VOID_RETURN_TYPE : objectMapper.writeValueAsString(returnObj);

    log.info(
      "uri={}；client={}；accountName={}；accountId={} -> {} [{}]；入参：{}；返回值：{}",
      reqUri,
      reqIp,
      operatorName,
      operatorId,
      methodMessage,
      qualifiedMethodName,
      paramJson,
      returnJson
    );

    OperationLogData logData = new OperationLogData(
      operatorId,
      operatorName,
      LocalDateTime.now(),
      reqIp,
      reqUri,
      qualifiedMethodName,
      methodMessage,
      paramJson,
      returnJson
    );

    logService.saveLog(logData);
  }

  private String getMethodMessage(JoinPoint joinPoint) {
    Method method = getMethod(joinPoint);

    return Optional.ofNullable(method.getAnnotation(OperationLogger.class))
      .map(OperationLogger::value)
      .orElseThrow(() -> new InternalException("不存在 @OperationLogger"));
  }

  private String getQualifiedMethodName(JoinPoint joinPoint) {
    Method method = getMethod(joinPoint);
    String methodName = method.getName();

    Class<?> clazz = joinPoint.getTarget().getClass();
    String className = clazz.getName();

    return StrUtil.format("{}.{}", className, methodName);
  }

  private Map<String, Object> getParameters(JoinPoint joinPoint) {
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

  private Method getMethod(JoinPoint joinPoint) {
    return getSignature(joinPoint).getMethod();
  }

  private MethodSignature getSignature(JoinPoint joinPoint) {
    return (MethodSignature) joinPoint.getSignature();
  }
}
