package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.shared.AuthUtils;
import net.wuxianjie.springbootcore.shared.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.exception.AbstractBaseException;
import net.wuxianjie.springbootcore.shared.exception.AbstractServerBaseException;
import net.wuxianjie.springbootcore.shared.util.NetUtils;
import org.apache.catalina.connector.ClientAbortException;
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
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

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
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    /**
     * 处理因 HTTP 请求的 MIME 类型与目标 API 不匹配而导致的异常。
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpMediaTypeNotAcceptableException(final HttpServletRequest request) {
        final String mimeTypes = Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
                .map(enumeration -> {
                    final List<String> accepts = new ArrayList<>();
                    while (enumeration.hasMoreElements()) {
                        accepts.add(enumeration.nextElement().trim());
                    }
                    return String.join(", ", accepts);
                })
                .orElse("null");

        final String message = StrUtil.format("API 不支持返回请求头指定的 MIME 类型 [{}: {}]",
                HttpHeaders.ACCEPT, mimeTypes);

        logWarnOrError(request, message, true);

        return buildResponseEntity(request, HttpStatus.NOT_ACCEPTABLE, message);
    }

    /**
     * 处理因 HTTP 请求的请求方法与目标 API 要求的请求方法不匹配而导致的异常。
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpRequestMethodNotSupportedException(final HttpServletRequest request) {
        final String message = StrUtil.format("API 不支持 {} 请求方法", request.getMethod());

        logWarnOrError(request, message, true);

        return buildResponseEntity(request, HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    /**
     * 处理因 HTTP 请求不是 Multipart Request，但 Controller 中参数存在 {@link MultipartFile} 而导致的异常。
     *
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiResult<Void>> handleMultipartException(final MultipartException e,
                                                                    final HttpServletRequest request) {

        final String message = StrUtil.format("API 要求请求必须为 Multipart Request");

        logWarnOrError(request, message, e, true);

        return buildResponseEntity(request, HttpStatus.NOT_ACCEPTABLE, message);
    }

    /**
     * 处理因无法解析 HTTP 请求体内容而导致的异常。
     *
     * @param e       {@link HttpMessageNotReadableException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e,
                                                                                 final HttpServletRequest request) {
        final String message = "请求体内容不合法";

        logWarnOrError(request, message, e, true);

        return buildResponseEntity(request, HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理因 HTTP 请求缺少必填参数而导致的异常。
     *
     * @param e       {@link MissingServletRequestParameterException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<Void>> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e,
                                                                                         final HttpServletRequest request) {
        final String message = StrUtil.format("缺少必填参数 {}", e.getParameterName());

        logWarnOrError(request, message, true);

        return buildResponseEntity(request, HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 处理因 HTTP 请求参数校验（{@link Validated}）不通过而导致的异常。
     *
     * @param e       {@link ConstraintViolationException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleConstraintViolationException(final ConstraintViolationException e,
                                                                              final HttpServletRequest request) {
        final List<String> logMessageList = new ArrayList<>();
        final List<String> messageList = new ArrayList<>();

        final Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (final ConstraintViolation<?> violation : violations) {
            final String message = violation.getMessage();
            messageList.add(message);

            final String queryParamPath = violation.getPropertyPath().toString();
            final String queryParam = queryParamPath.contains(".")
                    ? queryParamPath.substring(queryParamPath.indexOf(".") + 1)
                    : queryParamPath;
            final String logMsg = StrUtil.format("{} [{}={}]",
                    message, queryParam, violation.getInvalidValue());
            logMessageList.add(logMsg);
        }

        logParameterWarn(request, logMessageList);

        return buildResponseEntity(request, HttpStatus.BAD_REQUEST, String.join("；", messageList));
    }

    /**
     * 处理因 HTTP 请求参数校验不通过（{@link Valid}）而导致的异常。
     *
     * @param e       {@link BindException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<Void>> handleBindException(final BindException e,
                                                               final HttpServletRequest request) {
        final List<String> logMessageList = new ArrayList<>();
        final List<String> messageList = new ArrayList<>();

        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (final FieldError fieldError : fieldErrors) {
            final String message = fieldError.getDefaultMessage();
            messageList.add(message);

            final String logMsg = StrUtil.format("{} [{}={}]",
                    message, fieldError.getField(), fieldError.getRejectedValue());
            logMessageList.add(logMsg);
        }

        logParameterWarn(request, logMessageList);

        return buildResponseEntity(request, HttpStatus.BAD_REQUEST, String.join("；", messageList));
    }

    /**
     * 处理自定义异常，且不记录异常堆栈信息。
     *
     * @param e       {@link AbstractBaseException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<ApiResult<Void>> handleCustomException(final AbstractBaseException e,
                                                                 final HttpServletRequest request) {
        final String message = e.getMessage();
        final Throwable cause = e.getCause();
        final boolean isServerError = e instanceof AbstractServerBaseException;

        if (cause == null) {
            logWarnOrError(request, message, !isServerError);
        } else {
            logWarnOrError(request, message, cause, !isServerError);
        }

        return buildResponseEntity(request, e.getHttpStatus(), message);
    }

    /**
     * 处理 JDBC 操作异常。
     *
     * @param e       {@link UncategorizedDataAccessException}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(UncategorizedDataAccessException.class)
    public ResponseEntity<ApiResult<Void>> handleJdbcException(final UncategorizedDataAccessException e,
                                                               final HttpServletRequest request) {
        final String message = "数据库操作异常";

        logError(request, message, e);

        return buildResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 处理非正常关闭 socket 引发的错误。
     *
     * @param e       {@link ClientAbortException}
     * @param request {@link HttpServletRequest}
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException(final ClientAbortException e,
                                           final HttpServletRequest request) {
        final String message = "非正常关闭 socket";

        logError(request, message, e);
    }

    /**
     * 处理所有未被特定 {@code handleException(...)} 方法捕获的异常。
     *
     * @param e       {@link Throwable}
     * @param request {@link HttpServletRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResult<Void>> handleAllException(final Throwable e,
                                                              final HttpServletRequest request) {
        // 不要处理 AccessDeniedException，否则会导致 Spring Security 无法处理 403
        if (e instanceof AccessDeniedException) {
            throw (AccessDeniedException) e;
        }

        final String message = "服务异常";

        logError(request, message, e);

        return buildResponseEntity(request, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private boolean isJsonRequest(final HttpServletRequest request) {
        // 只有在 Accept 请求头中明确指定不包含 JSON 时才认为非 JSON 请求
        return Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
                .map(acceptEnum -> {
                    if (!acceptEnum.hasMoreElements()) return true;

                    while (acceptEnum.hasMoreElements()) {
                        final boolean containsJson = StrUtil.containsAnyIgnoreCase(acceptEnum.nextElement(),
                                MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE);

                        if (containsJson) return true;
                    }

                    return false;
                })
                .orElse(true);
    }

    private TokenUserDetails getCurrentUser() {
        return AuthUtils.getCurrentUser()
                .orElse(new TokenUserDetails() {
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

    private void logWarnOrError(final HttpServletRequest request,
                                final String message,
                                final boolean isWarn) {
        final TokenUserDetails currentUser = getCurrentUser();
        final String logMessage = StrUtil.format("uri={}；client={}；accountName={}；accountId={} -> {}",
                request.getRequestURI(), NetUtils.getRealIpAddress(request),
                currentUser.getAccountName(), currentUser.getAccountId(),
                message);

        if (isWarn) {
            log.warn(logMessage);
        } else {
            log.error(logMessage);
        }
    }

    private void logWarnOrError(final HttpServletRequest request,
                                final String message,
                                final Throwable e,
                                final boolean isWarn) {
        final TokenUserDetails currentUser = getCurrentUser();
        final String logMessage = StrUtil.format("uri={}；client={}；accountName={}；accountId={} -> {}：{}",
                request.getRequestURI(), NetUtils.getRealIpAddress(request),
                currentUser.getAccountName(), currentUser.getAccountId(),
                message, e.getMessage());

        if (isWarn) {
            log.warn(logMessage);
        } else {
            log.error(logMessage);
        }
    }

    private void logParameterWarn(final HttpServletRequest request,
                                  final List<String> messageList) {
        final TokenUserDetails currentUser = getCurrentUser();
        log.warn("uri={}；client={}；accountName={}；accountId={} -> 参数不合法：{}",
                request.getRequestURI(), NetUtils.getRealIpAddress(request),
                currentUser.getAccountName(), currentUser.getAccountId(),
                String.join("；", messageList));
    }

    private void logError(final HttpServletRequest request,
                          final String message,
                          final Throwable e) {
        final TokenUserDetails currentUser = getCurrentUser();
        log.error("uri={}；client={}；accountName={}；accountId={} -> {}",
                request.getRequestURI(), NetUtils.getRealIpAddress(request),
                currentUser.getAccountName(), currentUser.getAccountId(),
                message, e);
    }

    private ResponseEntity<ApiResult<Void>> buildResponseEntity(final HttpServletRequest request,
                                                                final HttpStatus httpStatus,
                                                                final String message) {
        if (isJsonRequest(request)) {
            return new ResponseEntity<>(ApiResultWrapper.fail(message), httpStatus);
        }

        return new ResponseEntity<>(null, httpStatus);
    }
}
