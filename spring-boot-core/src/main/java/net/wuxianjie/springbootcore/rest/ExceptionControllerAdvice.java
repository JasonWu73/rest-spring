package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.UserDetails;
import net.wuxianjie.springbootcore.shared.AbstractBaseException;
import net.wuxianjie.springbootcore.shared.InternalException;
import net.wuxianjie.springbootcore.shared.NetUtils;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controller 层全局异常处理。
 *
 * @author 吴仙杰
 * @see GlobalErrorController
 */
@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * 处理因 HTTP 请求的 MIME 类型与目标 API 不匹配而导致的异常。
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpMediaTypeNotAcceptableException(
            HttpServletRequest request
    ) {
        String mimeType = Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
                .map(acceptEnumeration -> {
                    List<String> accepts = new ArrayList<>();

                    while (acceptEnumeration.hasMoreElements()) {
                        accepts.add(acceptEnumeration.nextElement().trim());
                    }

                    return String.join(", ", accepts);
                })
                .orElse("null");

        String msg = StrUtil.format(
                "API 不支持返回请求头指定的 MIME 类型 [{}: {}]",
                HttpHeaders.ACCEPT,
                mimeType
        );

        UserDetails currentUser = getCurrentUser();

        log.warn(
                "uri={}；client={}；accountName={}；accountId={} -> {}",
                request.getRequestURI(),
                NetUtils.getRealIpAddress(request),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                msg
        );

        return buildResponseEntity(request, HttpStatus.NOT_ACCEPTABLE, msg);
    }

    /**
     * 处理因 HTTP 请求的请求方法与目标 API 要求的请求方法不匹配而导致的异常。
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpRequestMethodNotSupportedException(
            HttpServletRequest request
    ) {
        String msg = StrUtil.format(
                "API 不支持 {} 请求方法",
                request.getMethod()
        );

        UserDetails currentUser = getCurrentUser();

        log.warn(
                "uri={}；client={}；accountName={}；accountId={} -> {}",
                NetUtils.getRealIpAddress(request),
                request.getRequestURI(),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                msg
        );

        return buildResponseEntity(request, HttpStatus.METHOD_NOT_ALLOWED, msg);
    }

    /**
     * 处理因无法解析 HTTP 请求体内容而导致的异常。
     *
     * @param e       {@link HttpMessageNotReadableException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        String msg = "请求体内容不合法";

        UserDetails currentUser = getCurrentUser();

        log.warn(
                "uri={}；client={}；accountName={}；accountId={} -> {}：{}",
                NetUtils.getRealIpAddress(request),
                request.getRequestURI(),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                msg,
                e.getMessage()
        );

        return buildResponseEntity(request, HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 处理因 HTTP 请求缺少必填参数而导致的异常。
     *
     * @param e       {@link MissingServletRequestParameterException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e,
            HttpServletRequest request
    ) {
        String msg = StrUtil.format(
                "缺少必填参数 {}",
                e.getParameterName()
        );

        UserDetails currentUser = getCurrentUser();

        log.warn(
                "uri={}；client={}；accountName={}；accountId={} -> {}",
                NetUtils.getRealIpAddress(request),
                request.getRequestURI(),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                msg
        );

        return buildResponseEntity(request, HttpStatus.BAD_REQUEST, msg);
    }

    /**
     * 处理因 HTTP 请求参数校验（{@link Validated}）不通过而导致的异常。
     *
     * @param e       {@link ConstraintViolationException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraintViolationException(
            ConstraintViolationException e,
            HttpServletRequest request
    ) {
        List<String> logMsgList = new ArrayList<>();
        List<String> msgList = new ArrayList<>();

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String msg = violation.getMessage();

            msgList.add(msg);

            String queryParamPath = violation.getPropertyPath().toString();
            String queryParam = queryParamPath.contains(".")
                    ? queryParamPath.substring(queryParamPath.indexOf(".") + 1)
                    : queryParamPath;
            String logMsg = StrUtil.format(
                    "{} [{} = {}]",
                    msg,
                    queryParam,
                    violation.getInvalidValue()
            );

            logMsgList.add(logMsg);
        }

        UserDetails currentUser = getCurrentUser();

        log.warn(
                "uri={}；client={}；accountName={}；accountId={} -> 参数不合法：{}",
                NetUtils.getRealIpAddress(request),
                request.getRequestURI(),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                String.join("；", logMsgList)
        );

        return buildResponseEntity(
                request,
                HttpStatus.BAD_REQUEST,
                String.join("；", msgList)
        );
    }

    /**
     * 处理因 HTTP 请求参数校验不通过（{@link Valid}）而导致的异常。
     *
     * @param e       {@link BindException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<Void>> handleBindException(
            BindException e,
            HttpServletRequest request
    ) {
        List<String> logMsgList = new ArrayList<>();
        List<String> msgList = new ArrayList<>();

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            String msg = fieldError.getDefaultMessage();

            msgList.add(msg);

            String logMsg = StrUtil.format(
                    "{} [{} = {}]",
                    msg,
                    fieldError.getField(),
                    fieldError.getRejectedValue()
            );

            logMsgList.add(logMsg);
        }

        UserDetails currentUser = getCurrentUser();

        log.warn(
                "uri={}；client={}；accountName={}；accountId={} -> 参数不合法：{}",
                NetUtils.getRealIpAddress(request),
                request.getRequestURI(),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                String.join("；", logMsgList)
        );

        return buildResponseEntity(
                request,
                HttpStatus.BAD_REQUEST,
                String.join("；", msgList)
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
    public ResponseEntity<ApiResult<Void>> handleCustomException(
            AbstractBaseException e,
            HttpServletRequest request
    ) {
        Throwable cause = e.getCause();
        String msg = e.getMessage();
        UserDetails currentUser = getCurrentUser();
        String logMsg;

        if (cause == null) {
            logMsg = StrUtil.format(
                    "uri={}；client={}；accountName={}；accountId={} -> {}",
                    NetUtils.getRealIpAddress(request),
                    request.getRequestURI(),
                    currentUser.getAccountName(),
                    currentUser.getAccountId(),
                    msg
            );
        } else {
            logMsg = StrUtil.format(
                    "uri={}；client={}；accountName={}；accountId={} -> {}：{}",
                    NetUtils.getRealIpAddress(request),
                    request.getRequestURI(),
                    currentUser.getAccountName(),
                    currentUser.getAccountId(),
                    msg,
                    cause.getMessage()
            );
        }

        if (e instanceof InternalException) {
            log.error(logMsg);
        } else {
            log.warn(logMsg);
        }

        return buildResponseEntity(request, e.getHttpStatus(), msg);
    }

    /**
     * 处理 JDBC 操作异常。
     *
     * @param e       {@link UncategorizedDataAccessException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(UncategorizedDataAccessException.class)
    public ResponseEntity<ApiResult<Void>> handleJdbcException(
            UncategorizedDataAccessException e,
            HttpServletRequest request
    ) {
        String msg = "数据库操作异常";
        UserDetails currentUser = getCurrentUser();

        log.error(
                "uri={}；client={}；accountName={}；accountId={} -> {}",
                NetUtils.getRealIpAddress(request),
                request.getRequestURI(),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                msg,
                e
        );

        return buildResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }

    /**
     * 处理所有未被特定 {@code handleException(...)} 方法捕获的异常。
     *
     * @param e       {@link Throwable}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResult<Void>> handleAllException(
            Throwable e,
            HttpServletRequest request
    ) {
        if (e instanceof AccessDeniedException) {
            // 不要处理 AccessDeniedException，否则会导致 Spring Security 无法处理 403
            throw (AccessDeniedException) e;
        }

        String msg = "服务异常";
        UserDetails currentUser = getCurrentUser();

        log.error(
                "uri={}；client={}；accountName={}；accountId={} -> {}",
                NetUtils.getRealIpAddress(request),
                request.getRequestURI(),
                currentUser.getAccountName(),
                currentUser.getAccountId(),
                msg,
                e
        );

        return buildResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR, msg);
    }

    private ResponseEntity<ApiResult<Void>> buildResponseEntity(
            HttpServletRequest request,
            HttpStatus httpStatus,
            String msg
    ) {
        if (isJsonRequest(request)) {
            return new ResponseEntity<>(ApiResultWrapper.fail(msg), httpStatus);
        }

        return new ResponseEntity<>(null, httpStatus);
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        // 只有在明确指定 Accept 请求头，且不包含 JSON 时才认为非 JSON 请求
        return Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
                .map(acceptEnumeration -> {
                    if (!acceptEnumeration.hasMoreElements()) {
                        return true;
                    }

                    while (acceptEnumeration.hasMoreElements()) {
                        boolean containsJson = StrUtil.containsAnyIgnoreCase(
                                acceptEnumeration.nextElement(),
                                MediaType.ALL_VALUE,
                                MediaType.APPLICATION_JSON_VALUE
                        );

                        if (containsJson) {
                            return true;
                        }
                    }

                    return false;
                })
                .orElse(true);
    }

    private UserDetails getCurrentUser() {
        return AuthUtils.getCurrentUser()
                .orElse(new UserDetails() {
                    @Override
                    public Integer getAccountId() {
                        return null;
                    }

                    @Override
                    public String getAccountName() {
                        return null;
                    }

                    @Override
                    public String getRoles() {
                        return null;
                    }

                    @Override
                    public String getAccessToken() {
                        return null;
                    }

                    @Override
                    public String getRefreshToken() {
                        return null;
                    }
                });
    }
}
