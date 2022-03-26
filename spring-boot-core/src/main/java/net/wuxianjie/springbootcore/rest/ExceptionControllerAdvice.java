package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.shared.AbstractBaseException;
import net.wuxianjie.springbootcore.shared.InternalServerException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controller 层全局异常处理。
 *
 * @author 吴仙杰
 * @see GlobalErrorController
 */
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    /**
     * 处理因 HTTP 请求的 MIME 类型与目标 API 不匹配而导致的异常。
     *
     * @param e       {@link HttpMediaTypeException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ApiResult<Void>> handleException(HttpMediaTypeException e,
                                                           HttpServletRequest request) {
        String mimeType = Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
                .map(enumeration -> {
                    StringBuilder builder = new StringBuilder();
                    Iterator<String> iterator = enumeration.asIterator();

                    while (iterator.hasNext()) {
                        if (builder.length() > 0) {
                            builder.append(",");
                        }

                        builder.append(iterator.next());
                    }

                    return builder.toString();
                })
                .orElse("null");

        String msg = StrUtil.format(
                "API 不支持返回请求头指定的 MIME 类型 [{}: {}]",
                HttpHeaders.ACCEPT, mimeType);

        log.info("{} -> {}", request.getRequestURI(), msg);

        return createResponseEntity(request, HttpStatus.NOT_ACCEPTABLE, msg);
    }

    /**
     * 处理因 HTTP 请求的请求方法与目标 API 要求的请求方法不匹配而导致的异常。
     *
     * @param e       {@link HttpRequestMethodNotSupportedException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleException(HttpRequestMethodNotSupportedException e,
                                                           HttpServletRequest request) {
        String msg = StrUtil.format("API 不支持 {} 请求方法",
                request.getMethod());

        log.info("{} -> {}", request.getRequestURI(), msg);

        return createResponseEntity(request, HttpStatus.METHOD_NOT_ALLOWED, msg);
    }

    /**
     * 处理因 HTTP 请求缺少 {@code @RequestMapping} 中指定的请求头而导致的异常。
     *
     * @param e       {@link MissingRequestHeaderException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResult<Void>> handleException(MissingRequestHeaderException e,
                                                           HttpServletRequest request) {
        String msg = "缺少必要请求头";

        log.warn("HTTP 请求 [{}] -> {}：{}", request, msg, e.getMessage());

        return createResponseEntity(request, HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 处理因无法解析 HTTP 请求体内容而导致的异常。
     *
     * @param e       {@link HttpMessageNotReadableException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleException(HttpMessageNotReadableException e,
                                                           HttpServletRequest request) {
        String msg = "HTTP 请求体内容不合法";

        log.warn("HTTP 请求 [{}]  -> {}：{}", request, msg, e.getMessage());

        return createResponseEntity(request, HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 处理因 HTTP 请求缺少必填参数而导致的异常。
     *
     * @param e       {@link MissingServletRequestParameterException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<Void>> handleException(MissingServletRequestParameterException e,
                                                           HttpServletRequest request) {
        String msg = StrUtil.format("缺少必填参数 [{}]", e.getParameterName());

        log.warn("HTTP 请求 [{}]   -> {}", request, msg);

        return createResponseEntity(request, HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 处理因 HTTP 请求参数校验不通过而导致的异常。
     *
     * @param e       {@link ConstraintViolationException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        List<String> messageListToLog = new ArrayList<>();
        List<String> messageListToResponse = new ArrayList<>();

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String messageToResponse = violation.getMessage();

            messageListToResponse.add(messageToResponse);

            String messageToLog = StrUtil.format(
                    "{}.{} [拒绝值 [{}]   ：{}]   ",
                    violation.getRootBeanClass().getName(),
                    violation.getPropertyPath(),
                    violation.getInvalidValue(),
                    messageToResponse
            );

            messageListToLog.add(messageToLog);
        }

        log.warn("HTTP 请求 [{}]  -> 参数错误：{}",
                request, String.join("；", messageListToLog)
        );

        return createResponseEntity(request, HttpStatus.BAD_REQUEST,
                String.join("；", messageListToResponse)
        );
    }

    /**
     * 处理因 HTTP 请求参数校验不通过而导致的异常。
     *
     * @param e       {@link BindException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<Void>> handleException(BindException e,
                                                           HttpServletRequest request) {
        List<String> messageListToLog = new ArrayList<>();
        List<String> messageListToResponse = new ArrayList<>();

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            String messageToResponse = fieldError.getDefaultMessage();

            messageListToResponse.add(messageToResponse);

            String messageToLog = StrUtil.format(
                    "{}.{} [拒绝值 [{}]  ：{}]  ",
                    fieldError.getObjectName(),
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    messageToResponse
            );

            messageListToLog.add(messageToLog);
        }

        log.warn("HTTP 请求 [{}]  -> 参数错误：{}",
                request, String.join("；", messageListToLog)
        );

        return createResponseEntity(request, HttpStatus.BAD_REQUEST,
                String.join("；", messageListToResponse)
        );
    }

    /**
     * 处理自定义异常，且不记录异常堆栈信息。
     *
     * @param e       {@link AbstractBaseException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            AbstractBaseException e,
            HttpServletRequest request
    ) {
        Throwable cause = e.getCause();
        String messageToResponse = e.getMessage();

        String messageToLog;

        if (cause == null) {
            messageToLog = StrUtil.format("HTTP 请求 [{}]  -> {}",
                    request, messageToResponse
            );
        } else {
            messageToLog = StrUtil.format("HTTP 请求 [{}]  -> {}：{}",
                    request, messageToResponse, cause.getMessage()
            );
        }

        if (e instanceof InternalServerException) {
            log.error(messageToLog);
        } else {
            log.warn(messageToLog);
        }

        return createResponseEntity(
                request, e.getHttpStatus(), messageToResponse
        );
    }

    /**
     * 处理 JDBC 操作异常。
     *
     * @param e       {@link UncategorizedDataAccessException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(UncategorizedDataAccessException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            UncategorizedDataAccessException e,
            HttpServletRequest request
    ) {
        String messageToResponse = "数据库操作异常";

        log.error("HTTP 请求 [{}]  -> {}", request, messageToResponse, e);

        return createResponseEntity(
                request, HttpStatus.INTERNAL_SERVER_ERROR, messageToResponse
        );
    }

    /**
     * 处理所有未被特定 {@code handleException(...)} 方法捕获的异常。
     *
     * @param e       {@link Throwable}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            Throwable e,
            HttpServletRequest request
    ) {
        if (e instanceof AccessDeniedException) {
            // 不要处理 AccessDeniedException，否则会导致 Spring Security 无法处理 403
            throw (AccessDeniedException) e;
        }

        String messageToResponse = "服务异常";

        log.error("HTTP 请求 [{}]  -> {}", request, messageToResponse, e);

        return createResponseEntity(
                request, HttpStatus.INTERNAL_SERVER_ERROR, messageToResponse
        );
    }

    private ResponseEntity<ApiResult<Void>> createResponseEntity(HttpServletRequest request,
                                                                 HttpStatus httpStatus,
                                                                 String errorMessage) {
        if (isJsonRequest(request)) {
            return new ResponseEntity<>(ApiResultWrapper.fail(errorMessage), httpStatus);
        }

        return new ResponseEntity<>(null, httpStatus);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
                .map(enumeration -> {
                            Iterator<String> iterator = enumeration.asIterator();

                            while (iterator.hasNext()) {
                                boolean containsJson =
                                        StrUtil.containsAnyIgnoreCase(
                                                iterator.next(),
                                                MediaType.ALL_VALUE,
                                                MediaType.APPLICATION_JSON_VALUE);

                                if (containsJson) {
                                    return true;
                                }
                            }

                            return false;
                        }
                )
                .orElse(true);
    }
}
