package net.wuxianjie.core.aspect;

import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.dto.ResponseDto;
import net.wuxianjie.core.exception.AbstractBaseException;
import net.wuxianjie.core.exception.InternalServerException;
import net.wuxianjie.core.util.ResponseDtoWrapper;
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
public class ControllerExceptionHandler {

    private static final String HTTP_HEADER_ACCEPT = "accept";
    private static final String ACCEPT_ALL = "*/*";
    private static final String ACCEPT_JSON = "application/json";

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ResponseDto<Void>> handleHttpMediaTypeException(
            final HttpMediaTypeException e,
            final WebRequest request
    ) {
        final String[] accepts = request.getHeaderValues(HTTP_HEADER_ACCEPT);

        log.warn("HTTP 请求【{}】-> 不支持请求头 accept 中指定的资源类型【{}】：{}",
                request,
                Arrays.toString(accepts),
                e.getMessage()
        );

        return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseDto<Void>> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException e,
            final WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> API 不支持当前 HTTP 请求方法：{}", request, e.getMessage());

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail("API 不支持当前 HTTP 请求方法"),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ResponseDto<Void>> handleMissingRequestHeaderException(
            final MissingRequestHeaderException e,
            final WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> 缺少必要请求头：{}", request, e.getMessage());

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail("缺少必要请求头"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<Void>> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException e,
            final WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> 请求体内容有误：{}", request, e.getMessage());

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail("请求体内容有误"),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseDto<Void>> handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException e,
            final WebRequest request
    ) {
        final String parameterName = e.getParameterName();

        log.warn("HTTP 请求【{}】-> 缺少必填参数【{}】", request, parameterName);

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail(String.format("缺少必填参数【%s】", parameterName)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<Void>> handleConstraintViolationException(
            final ConstraintViolationException e,
            final WebRequest request
    ) {
        final List<String> errorsToLog = new ArrayList<>();
        final List<String> errorsToResponse = new ArrayList<>();
        final Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();

        for (final ConstraintViolation<?> violation : constraintViolations) {
            final String error = String.format("%s.%s【拒绝值【%s】：%s】",
                    violation.getRootBeanClass().getName(),
                    violation.getPropertyPath(),
                    violation.getInvalidValue(),
                    violation.getMessage()
            );

            errorsToLog.add(error);

            errorsToResponse.add(violation.getMessage());
        }

        log.warn("HTTP 请求【{}】-> 参数错误：{}", request, String.join("；", errorsToLog));

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail(String.join("；", errorsToResponse)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDto<Void>> handleBindException(
            final BindException e,
            final WebRequest request
    ) {
        final List<String> errorsToLog = new ArrayList<>();
        final List<String> errorsToResponse = new ArrayList<>();
        final BindingResult bindingResult = e.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        for (final FieldError err : fieldErrors) {
            final String errMsg = String.format("%s.%s【拒绝值【%s】：%s】",
                    err.getObjectName(),
                    err.getField(),
                    err.getRejectedValue(),
                    err.getDefaultMessage());

            errorsToLog.add(errMsg);

            errorsToResponse.add(err.getDefaultMessage());
        }

        log.warn("HTTP 请求【{}】-> 参数错误：{}", request, String.join("；", errorsToLog));

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail(String.join("；", errorsToResponse)),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UncategorizedDataAccessException.class)
    public ResponseEntity<ResponseDto<Void>> handleUncategorizedDataAccessException(
            final UncategorizedDataAccessException e,
            final WebRequest request) {
        log.error("HTTP 请求【{}】-> 数据库操作异常", request, e);

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail("数据库操作异常"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<ResponseDto<Void>> handleAbstractBaseException(
            final AbstractBaseException e,
            final WebRequest request
    ) {
        final Throwable cause = e.getCause();

        if (cause == null) {
            log.warn("HTTP 请求【{}】-> {}", request, e.getMessage());
        } else {
            log.warn("HTTP 请求【{}】-> {}：{}", request, e.getMessage(), cause.getMessage());
        }

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, e.getHttpStatus());
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail(e.getMessage()),
                e.getHttpStatus()
        );
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ResponseDto<Void>> handleInternalServerException(
            final InternalServerException e,
            final WebRequest request
    ) {
        log.warn("HTTP 请求【{}】-> 服务异常（已知）：{}", request, e.getMessage());

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, e.getHttpStatus());
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail("服务异常（已知）"),
                e.getHttpStatus()
        );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ResponseDto<Void>> handleThrowable(
            final Throwable e,
            final WebRequest request
    ) {
        if (e instanceof AccessDeniedException) {
            // 不要让全局异常处理阻止了 Spring Security 的 403 处理
            throw (AccessDeniedException) e;
        }

        log.error("HTTP 请求【{}】-> 服务异常（未知）", request, e);

        if (isNotJsonResponse(request)) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(
                ResponseDtoWrapper.fail("服务异常（未知）"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private boolean isNotJsonResponse(final WebRequest request) {
        final String[] accepts = request.getHeaderValues(HTTP_HEADER_ACCEPT);

        if (accepts == null) {
            return true;
        }

        return Arrays.stream(accepts)
                .noneMatch(x -> x.contains(ACCEPT_ALL) ||
                        x.toLowerCase().contains(ACCEPT_JSON)
                );
    }
}
