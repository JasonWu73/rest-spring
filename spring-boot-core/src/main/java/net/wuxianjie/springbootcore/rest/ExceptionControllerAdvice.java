package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.ArrayUtil;
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
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            HttpMediaTypeException e,
            WebRequest request
    ) {
        String[] acceptArray = request.getHeaderValues(HttpHeaders.ACCEPT);
        String mimeTypeStr = acceptArray == null
                ? "null"
                : ArrayUtil.join(acceptArray, ",");

        log.warn("HTTP 请求【{}】-> 不支持请求头 {} 中指定的 MIME 类型【{}】：{}",
                request, HttpHeaders.ACCEPT, mimeTypeStr, e.getMessage()
        );

        return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
    }

    /**
     * 处理因 HTTP 请求的请求方法与目标 API 不匹配而导致的异常。
     *
     * @param e       {@link HttpRequestMethodNotSupportedException}
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            HttpRequestMethodNotSupportedException e,
            WebRequest request
    ) {
        String messageToResponse = "API 不支持当前请求方法";

        log.warn("HTTP 请求【{}】-> {}：{}",
                request, messageToResponse, e.getMessage()
        );

        return createResponseEntity(
                request, HttpStatus.METHOD_NOT_ALLOWED, messageToResponse
        );
    }

    /**
     * 处理因 HTTP 请求缺少 {@code @RequestMapping} 中指定的请求头而导致的异常。
     *
     * @param e       {@link MissingRequestHeaderException}
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            MissingRequestHeaderException e,
            WebRequest request
    ) {
        String messageToResponse = "缺少必要请求头";

        log.warn("HTTP 请求【{}】-> {}：{}",
                request, messageToResponse, e.getMessage()
        );

        return createResponseEntity(
                request, HttpStatus.BAD_REQUEST, messageToResponse
        );
    }

    /**
     * 处理因无法解析 HTTP 请求体内容而导致的异常。
     *
     * @param e       {@link HttpMessageNotReadableException}
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            HttpMessageNotReadableException e,
            WebRequest request
    ) {
        String messageToResponse = "HTTP 请求体内容不合法";

        log.warn("HTTP 请求【{}】-> {}：{}",
                request, messageToResponse, e.getMessage()
        );

        return createResponseEntity(
                request, HttpStatus.BAD_REQUEST, messageToResponse
        );
    }

    /**
     * 处理因 HTTP 请求缺少必填参数而导致的异常。
     *
     * @param e       {@link MissingServletRequestParameterException}
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            MissingServletRequestParameterException e,
            WebRequest request
    ) {
        String messageToResponse = StrUtil.format(
                "缺少必填参数【{}】", e.getParameterName()
        );

        log.warn("HTTP 请求【{}】-> {}", request, messageToResponse);

        return createResponseEntity(
                request, HttpStatus.BAD_REQUEST, messageToResponse
        );
    }

    /**
     * 处理因 HTTP 请求参数校验不通过而导致的异常。
     *
     * @param e       {@link ConstraintViolationException}
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            ConstraintViolationException e,
            WebRequest request
    ) {
        List<String> messageListToLog = new ArrayList<>();
        List<String> messageListToResponse = new ArrayList<>();

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String messageToResponse = violation.getMessage();

            messageListToResponse.add(messageToResponse);

            String messageToLog = StrUtil.format(
                    "{}.{}【拒绝值【{}】：{}】",
                    violation.getRootBeanClass().getName(),
                    violation.getPropertyPath(),
                    violation.getInvalidValue(),
                    messageToResponse
            );

            messageListToLog.add(messageToLog);
        }

        log.warn("HTTP 请求【{}】-> 参数错误：{}",
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
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<Void>> handleException(BindException e,
                                                           WebRequest request) {
        List<String> messageListToLog = new ArrayList<>();
        List<String> messageListToResponse = new ArrayList<>();

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            String messageToResponse = fieldError.getDefaultMessage();

            messageListToResponse.add(messageToResponse);

            String messageToLog = StrUtil.format(
                    "{}.{}【拒绝值【{}】：{}】",
                    fieldError.getObjectName(),
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    messageToResponse
            );

            messageListToLog.add(messageToLog);
        }

        log.warn("HTTP 请求【{}】-> 参数错误：{}",
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
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            AbstractBaseException e,
            WebRequest request
    ) {
        Throwable cause = e.getCause();
        String messageToResponse = e.getMessage();

        String messageToLog;

        if (cause == null) {
            messageToLog = StrUtil.format("HTTP 请求【{}】-> {}",
                    request, messageToResponse
            );
        } else {
            messageToLog = StrUtil.format("HTTP 请求【{}】-> {}：{}",
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
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(UncategorizedDataAccessException.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            UncategorizedDataAccessException e,
            WebRequest request
    ) {
        String messageToResponse = "数据库操作异常";

        log.error("HTTP 请求【{}】-> {}", request, messageToResponse, e);

        return createResponseEntity(
                request, HttpStatus.INTERNAL_SERVER_ERROR, messageToResponse
        );
    }

    /**
     * 处理所有未被特定 {@code handleException(...)} 方法捕获的异常。
     *
     * @param e       {@link Throwable}
     * @param request {@link WebRequest}
     * @return {@link ResponseEntity}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResult<Void>> handleException(
            Throwable e,
            WebRequest request
    ) {
        if (e instanceof AccessDeniedException) {
            // 不要处理 AccessDeniedException，否则会导致 Spring Security 无法处理 403
            throw (AccessDeniedException) e;
        }

        String messageToResponse = "服务异常";

        log.error("HTTP 请求【{}】-> {}", request, messageToResponse, e);

        return createResponseEntity(
                request, HttpStatus.INTERNAL_SERVER_ERROR, messageToResponse
        );
    }

    private ResponseEntity<ApiResult<Void>> createResponseEntity(
            WebRequest request,
            HttpStatus httpStatus,
            String errorMessage
    ) {
        if (isNotJsonRequest(request)) {
            return new ResponseEntity<>(null, httpStatus);
        }

        return new ResponseEntity<>(
                ApiResultWrapper.fail(errorMessage), httpStatus
        );
    }

    private boolean isNotJsonRequest(WebRequest request) {
        String[] acceptArray = request.getHeaderValues(HttpHeaders.ACCEPT);

        if (acceptArray == null) {
            return true;
        }

        return Arrays.stream(acceptArray)
                .noneMatch(accept -> StrUtil.containsAnyIgnoreCase(accept,
                                MediaType.ALL_VALUE,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                );
    }
}
