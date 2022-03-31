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
     * @throws JsonProcessingException 当对入参或返回值执行 JSON 序列化时出错时
     */
    @AfterReturning(pointcut = "@annotation(Logger)", returning = "returnObj")
    public void log(JoinPoint joinPoint, Object returnObj) throws JsonProcessingException {
        // 请求信息
        Optional<HttpServletRequest> reqOpt = NetUtils.getRequest();
        String reqIp = reqOpt.map(NetUtils::getRealIpAddress).orElse(null);
        String reqUri = reqOpt.map(HttpServletRequest::getRequestURI).orElse(null);

        // 用户信息
        Optional<UserDetails> userOpt = AuthUtils.getCurrentUser();
        Integer oprId = userOpt.map(UserDetails::getAccountId).orElse(null);
        String oprName = userOpt.map(UserDetails::getAccountName).orElse(null);

        // 方法信息
        String methodMsg = getMethodMessage(joinPoint);
        String methodName = getFullyQualifiedMethodName(joinPoint);
        String paramJson = objectMapper.writeValueAsString(getParams(joinPoint));
        String rtnJson = isVoidReturnType(joinPoint) ? "void" : objectMapper.writeValueAsString(returnObj);

        log.info("uri={}；client={}；accountName={}；accountId={} -> {} [{}]；入参：{}；返回值：{}",
                reqUri, reqIp, oprName, oprId, methodMsg, methodName, paramJson, rtnJson);

        logService.saveLog(new OperationLog(oprId, oprName, reqIp, reqUri, methodName, methodMsg, paramJson, rtnJson));
    }

    private String getMethodMessage(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return Optional.ofNullable(method.getAnnotation(Logger.class))
                .map(Logger::value)
                .orElseThrow(() -> new InternalException("无法获取 Logger 注解"));
    }

    private String getFullyQualifiedMethodName(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String methodName = method.getName();

        Class<?> clazz = joinPoint.getTarget().getClass();
        String className = clazz.getName();

        return StrUtil.format("{}.{}", className, methodName);
    }

    private Map<String, Object> getParams(JoinPoint joinPoint) {
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        return Optional.ofNullable(joinPoint.getArgs())
                .map(args -> {
                    String[] paramNames = methodSig.getParameterNames();
                    return ArrayUtil.zip(paramNames, args, true);
                })
                .orElse(new HashMap<>());
    }

    private boolean isVoidReturnType(JoinPoint joinPoint) {
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        String rtnType = methodSig.getReturnType().toString();
        return StrUtil.equalsIgnoreCase(rtnType, "void");
    }
}
