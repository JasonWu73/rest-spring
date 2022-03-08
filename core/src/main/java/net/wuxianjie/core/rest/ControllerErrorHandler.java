package net.wuxianjie.core.rest;

import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.exception.AbstractBaseException;
import net.wuxianjie.core.exception.InternalServerException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
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

@Slf4j
@ControllerAdvice
public class ControllerErrorHandler {

    private static final String HTTP_HEADER_ACCEPT_KEY = "accept";
    private static final String HTTP_HEADER_ACCEPT_ALL_VALUE = "*/*";
    private static final String HTTP_HEADER_ACCEPT_JSON_VALUE = "application/json";

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<RestData<Void>> handleHttpMediaTypeException(
            HttpMediaTypeException e,
            WebRequest request
    ) {
        String[] accepts = request.getHeaderValues(HTTP_HEADER_ACCEPT_KEY);

        log.warn("HTTP 请求【{}】-> 不支持请求头 accept 中指定的资源类型【{}】：{}",
                request,
                Arrays.toString(accepts),
                e.getMessage()
        );

        return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RestData<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e,
            WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> API 不支持当前 HTTP 请求方法：{}",
                request,
                e.getMessage()
        );

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail("API 不支持当前 HTTP 请求方法"),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<RestData<Void>> handleMissingRequestHeaderException(
            MissingRequestHeaderException e,
            WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> 缺少必要请求头：{}", request, e.getMessage());

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail("缺少必要请求头"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestData<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e,
            WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> 请求体内容有误：{}", request, e.getMessage());

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail("请求体内容有误"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestData<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e,
            WebRequest request
    ) {
        String parameterName = e.getParameterName();

        log.warn("HTTP 请求【{}】-> 缺少必填参数【{}】", request, parameterName);

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail(String.format("缺少必填参数【%s】", parameterName)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestData<Void>> handleConstraintViolationException(
            ConstraintViolationException e,
            WebRequest request
    ) {
        List<String> errorsToLog = new ArrayList<>();
        List<String> errorsToResponse = new ArrayList<>();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        for (ConstraintViolation<?> violation : violations) {
            String error = String.format("%s.%s【拒绝值【%s】：%s】",
                    violation.getRootBeanClass().getName(),
                    violation.getPropertyPath(),
                    violation.getInvalidValue(),
                    violation.getMessage()
            );

            errorsToLog.add(error);

            errorsToResponse.add(violation.getMessage());
        }

        log.warn("HTTP 请求【{}】-> 参数错误：{}",
                request,
                String.join("；", errorsToLog)
        );

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail(String.join("；", errorsToResponse)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<RestData<Void>> handleBindException(
            BindException e,
            WebRequest request
    ) {
        List<String> errorsToLog = new ArrayList<>();
        List<String> errorsToResponse = new ArrayList<>();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (FieldError err : fieldErrors) {
            String errMsg = String.format("%s.%s【拒绝值【%s】：%s】",
                    err.getObjectName(),
                    err.getField(),
                    err.getRejectedValue(),
                    err.getDefaultMessage());

            errorsToLog.add(errMsg);

            errorsToResponse.add(err.getDefaultMessage());
        }

        log.warn("HTTP 请求【{}】-> 参数错误：{}",
                request,
                String.join("；", errorsToLog)
        );

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail(String.join("；", errorsToResponse)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UncategorizedDataAccessException.class)
    public ResponseEntity<RestData<Void>> handleUncategorizedDataAccessException(
            UncategorizedDataAccessException e,
            WebRequest request
    ) {
        log.error("HTTP 请求【{}】-> 数据库操作异常", request, e);

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail("数据库操作异常"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<RestData<Void>> handleAbstractBaseException(
            AbstractBaseException e,
            WebRequest request
    ) {
        Throwable cause = e.getCause();

        if (cause == null) {
            log.warn("HTTP 请求【{}】-> {}", request, e.getMessage());
        } else {
            log.warn("HTTP 请求【{}】-> {}：{}",
                    request,
                    e.getMessage(),
                    cause.getMessage()
            );
        }

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, e.getHttpStatus());
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail(e.getMessage()),
                e.getHttpStatus()
        );
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<RestData<Void>> handleInternalServerException(
            InternalServerException e,
            WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> 服务异常（已知）：{}",
                request,
                e.getMessage()
        );

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, e.getHttpStatus());
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail("服务异常（已知）"),
                e.getHttpStatus()
        );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestData<Void>> handleThrowable(
            Throwable e,
            WebRequest request
    ) {
        if (e instanceof AccessDeniedException) {
            // 不要让全局异常处理 AccessDeniedException，
            // 否则将导致 Spring Security 无法正常处理 403
            throw (AccessDeniedException) e;
        }

        log.error("HTTP 请求【{}】-> 服务异常（未知）", request, e);

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                RestDataWrapper.fail("服务异常（未知）"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private boolean isNotJsonResponse(WebRequest request) {
        String[] accepts = request.getHeaderValues(HTTP_HEADER_ACCEPT_KEY);

        if (accepts == null) {
            return true;
        }

        return Arrays.stream(accepts)
                .noneMatch(x ->
                        x.contains(HTTP_HEADER_ACCEPT_ALL_VALUE) ||
                                x.toLowerCase().contains(HTTP_HEADER_ACCEPT_JSON_VALUE)
                );
    }
}
