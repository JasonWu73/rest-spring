package net.wuxianjie.springbootcore.log;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.UserDetails;
import net.wuxianjie.springbootcore.shared.InternalException;
import net.wuxianjie.springbootcore.shared.NetUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 记录操作日志。
 *
 * @author 吴仙杰
 * @see Logger
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final ObjectMapper objectMapper;
    private final OperationService logService;

    /**
     * 对标有 {@link Logger} 注解的方法记录操作日志。
     *
     * @param joinPoint {@link JoinPoint}
     * @param returnObj 方法返回值
     * @throws JsonProcessingException 当对入参或返回值执行 JSON 序列化时出错
     */
    @AfterReturning(pointcut = "@annotation(Logger)", returning = "returnObj")
    public void log(JoinPoint joinPoint, Object returnObj) throws JsonProcessingException {
        Optional<HttpServletRequest> requestOptional = NetUtils.getRequest();
        Optional<UserDetails> userOptional = AuthUtils.getCurrentUser();

        // 操作员 ID，当为开放 API 时，则值为 null
        Integer operatorId = userOptional
                .map(UserDetails::getAccountId)
                .orElse(null);
        // 操作员账号，当为开放 API 时，则值为 null
        String operatorName = userOptional
                .map(UserDetails::getAccountName)
                .orElse(null);
        // 请求 IP
        String requestIp = requestOptional.isPresent()
                ? NetUtils.getRealIpAddress(requestOptional.get())
                : "无法获取 IP";
        // 请求 URI
        String requestUri = requestOptional.isPresent()
                ? requestOptional.get().getRequestURI()
                : "无法获取请求 URI";
        // 目标方法的全限定名
        String methodName = getFullyQualifiedMethodName(joinPoint);
        // 目标方法的描述，即操作描述
        String methodMsg = getMethodMessage(joinPoint);
        // 入参
        Map<String, Object> params = getParams(joinPoint);
        String paramJson = params.isEmpty()
                ? "无参"
                : objectMapper.writeValueAsString(params);
        // 返回值
        String returnJson = isVoidReturnType(joinPoint)
                ? "void"
                : objectMapper.writeValueAsString(returnObj);

        log.info(
                "uri={}；client={}；accountName={}；accountId={} -> {} [{}]；入参：{}；返回值：{}",
                requestUri,
                requestIp,
                operatorName,
                operatorId,
                methodMsg,
                methodName,
                paramJson,
                returnJson
        );

        logService.saveLog(new OperationLog(
                operatorId,
                operatorName,
                requestIp,
                requestUri,
                methodName,
                methodMsg,
                paramJson,
                returnJson
        ));
    }

    private String getMethodMessage(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        Logger logger = Optional.ofNullable(method.getAnnotation(Logger.class))
                .orElseThrow(() -> new InternalException("无法获取 Logger 注解"));
        return logger.value();
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = getMethodSignature(joinPoint);
        return signature.getMethod();
    }

    private MethodSignature getMethodSignature(JoinPoint joinPoint) {
        return (MethodSignature) joinPoint.getSignature();
    }

    private Map<String, Object> getParams(JoinPoint joinPoint) {
        MethodSignature methodSignature = getMethodSignature(joinPoint);

        return Optional.ofNullable(joinPoint.getArgs())
                .map(args -> {
                    String[] paramNames = methodSignature.getParameterNames();
                    return ArrayUtil.zip(paramNames, args, true);
                })
                .orElse(new HashMap<>());
    }

    private String getFullyQualifiedMethodName(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();

        return StrUtil.format("{}.{}", className, methodName);
    }

    private boolean isVoidReturnType(JoinPoint joinPoint) {
        MethodSignature methodSignature = getMethodSignature(joinPoint);
        String returnType = methodSignature.getReturnType().toString();

        return StrUtil.equalsIgnoreCase(returnType, "void");
    }
}
