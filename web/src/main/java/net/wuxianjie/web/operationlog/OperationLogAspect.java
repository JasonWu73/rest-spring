package net.wuxianjie.web.operationlog;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.exception.InternalException;
import net.wuxianjie.springbootcore.security.AuthenticationUtils;
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
 * @see OperationLogger
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

  private static final String VOID_RETURN_TYPE = "void";

  private final ObjectMapper objectMapper;
  private final OperationLogService operationLogService;

  /**
   * 对标有 {@link OperationLogger} 注解的方法记录操作日志。
   *
   * @param joinPoint    {@link JoinPoint}
   * @param returnObject 方法返回值
   * @throws JsonProcessingException 当对入参或返回值执行 JSON 序列化出错时抛出
   */
  @AfterReturning(pointcut = "@annotation(net.wuxianjie.web.operationlog.OperationLogger)", returning = "returnObject")
  public void log(JoinPoint joinPoint, Object returnObject) throws JsonProcessingException {
    // 请求信息
    Optional<HttpServletRequest> requestOptional = NetUtils.getRequest();
    String requestIp = requestOptional.map(NetUtils::getRealIpAddress).orElse(null);
    String requestUri = requestOptional.map(HttpServletRequest::getRequestURI).orElse(null);

    // 用户信息
    Optional<TokenUserDetails> userOptional = AuthenticationUtils.getCurrentUser();
    Integer userId = userOptional.map(TokenUserDetails::getUserId).orElse(null);
    String username = userOptional.map(TokenUserDetails::getUsername).orElse(null);

    // 方法信息
    String methodMessage = getMethodMessage(joinPoint);
    String qualifiedMethodName = getQualifiedMethodName(joinPoint);
    Map<String, Object> params = getParameters(joinPoint);
    String parameterJson = objectMapper.writeValueAsString(params);
    String returnJson = isVoidReturnType(joinPoint)
      ? VOID_RETURN_TYPE
      : objectMapper.writeValueAsString(returnObject);

    // 打印到控制台
    log.info("{} [{}]，入参：{}，返回值：{}，客户端信息：uri={};client={};user={}",
      methodMessage, qualifiedMethodName, parameterJson, returnJson, requestUri, requestIp, username);

    // 保存操作日志数据
    OperationLog LogToSave = new OperationLog();
    LogToSave.setOperationTime(LocalDateTime.now());
    LogToSave.setUserId(userId);
    LogToSave.setUsername(username);
    LogToSave.setRequestIp(requestIp);
    LogToSave.setRequestUri(requestUri);
    LogToSave.setMethodName(qualifiedMethodName);
    LogToSave.setMethodMessage(methodMessage);
    LogToSave.setParameterJson(parameterJson);
    LogToSave.setReturnJson(returnJson);
    operationLogService.saveOpLog(LogToSave);
  }

  private MethodSignature getSignature(JoinPoint joinPoint) {
    return (MethodSignature) joinPoint.getSignature();
  }

  private Method getMethod(JoinPoint joinPoint) {
    return getSignature(joinPoint).getMethod();
  }

  private String getMethodMessage(JoinPoint joinPoint) {
    Method method = getMethod(joinPoint);
    return Optional.ofNullable(method.getAnnotation(OperationLogger.class))
      .map(OperationLogger::value)
      .orElseThrow(() -> new InternalException(StrUtil.format("未找到 @{} 注解", OperationLogger.class.getSimpleName())));
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
            if (arg instanceof MultipartFile) return ((MultipartFile) arg).getOriginalFilename();

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
