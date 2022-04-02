package net.wuxianjie.springbootcore.operationlog;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.shared.AuthUtils;
import net.wuxianjie.springbootcore.shared.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.exception.InternalException;
import net.wuxianjie.springbootcore.shared.util.NetUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 记录操作日志。
 *
 * @author 吴仙杰
 * @see OperationLogger
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class OperationLogAspect {
    
    static final String VOID_RETURN_TYPE = "void";

    private final ObjectMapper objectMapper;
    private final OperationLogService logService;

    /**
     * 对标有 {@link OperationLogger} 注解的方法记录操作日志。
     *
     * @param joinPoint    {@link JoinPoint}
     * @param returnObject 方法返回值
     * @throws JsonProcessingException 当对入参或返回值执行 JSON 序列化时出错时
     */
    @AfterReturning(pointcut = "@annotation(net.wuxianjie.springbootcore.operationlog.OperationLogger)",
            returning = "returnObject")
    public void log(final JoinPoint joinPoint,
                    final Object returnObject) throws JsonProcessingException {
        // 请求信息
        final Optional<HttpServletRequest> requestOptional = NetUtils.getRequest();
        final String requestIp = requestOptional.map(NetUtils::getRealIpAddress).orElse(null);
        final String requestUri = requestOptional.map(HttpServletRequest::getRequestURI).orElse(null);

        // 用户信息
        final Optional<TokenUserDetails> userDetailsOptional = AuthUtils.getCurrentUser();
        final Integer operatorId = userDetailsOptional.map(TokenUserDetails::getAccountId).orElse(null);
        final String operatorName = userDetailsOptional.map(TokenUserDetails::getAccountName).orElse(null);

        // 方法信息
        final String methodMessage = getMethodMessage(joinPoint);
        final String qualifiedMethodName = getQualifiedMethodName(joinPoint);
        final String parameterJson = objectMapper.writeValueAsString(getParameters(joinPoint));
        final String returnJson = isVoidReturnType(joinPoint)
                ? VOID_RETURN_TYPE
                : objectMapper.writeValueAsString(returnObject);

        log.info("uri={}；client={}；accountName={}；accountId={} -> {} [{}]；入参：{}；返回值：{}",
                requestUri, requestIp, operatorName, operatorId,
                methodMessage, qualifiedMethodName, parameterJson, returnJson);

        final OperationLogData logData = new OperationLogData(
                operatorId,
                operatorName,
                LocalDateTime.now(),
                requestIp,
                requestUri,
                qualifiedMethodName,
                methodMessage,
                parameterJson,
                returnJson
        );
        logService.saveLog(logData);
    }

    private String getMethodMessage(final JoinPoint joinPoint) {
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return Optional.ofNullable(method.getAnnotation(OperationLogger.class))
                .map(OperationLogger::value)
                .orElseThrow(() -> new InternalException(
                        StrUtil.format("无法获取 {} 注解", OperationLogger.class.getName())
                ));
    }

    private String getQualifiedMethodName(final JoinPoint joinPoint) {
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        final String methodName = method.getName();

        final Class<?> clazz = joinPoint.getTarget().getClass();
        final String className = clazz.getName();

        return StrUtil.format("{}.{}", className, methodName);
    }

    private Map<String, Object> getParameters(final JoinPoint joinPoint) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return Optional.ofNullable(joinPoint.getArgs())
                .map(args -> {
                    final String[] parameterNames = methodSignature.getParameterNames();
                    return ArrayUtil.zip(parameterNames, args, true);
                })
                .orElse(new HashMap<>());
    }

    private boolean isVoidReturnType(final JoinPoint joinPoint) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String returnType = methodSignature.getReturnType().toString();
        return StrUtil.equalsIgnoreCase(returnType, VOID_RETURN_TYPE);
    }
}
