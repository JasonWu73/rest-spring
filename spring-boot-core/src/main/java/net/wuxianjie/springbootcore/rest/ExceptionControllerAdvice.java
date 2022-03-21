package net.wuxianjie.springbootcore.rest;

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
import java.util.*;

/**
 * Controller 层全局异常处理。
 *
 * @author 吴仙杰
 * @see GlobalErrorController
 */
@Slf4j
@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<RestData<Void>> handleException(HttpMediaTypeException e, WebRequest request) {
        String[] accepts = request.getHeaderValues(HttpHeaders.ACCEPT);
        log.warn("HTTP 请求【{}】-> 不支持请求头 {} 中指定的资源类型【{}】：{}",
                request, HttpHeaders.ACCEPT, Arrays.toString(accepts), e.getMessage());

        return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RestData<Void>> handleException(HttpRequestMethodNotSupportedException e, WebRequest request) {
        String messageToResponse = "API 不支持当前 HTTP 请求方法";
        String messageToLog = String.format("HTTP 请求【%s】-> %s：%s", request, messageToResponse, e.getMessage());
        return getRestDataResponseEntity(request, HttpStatus.METHOD_NOT_ALLOWED, messageToResponse, messageToLog);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<RestData<Void>> handleException(MissingRequestHeaderException e, WebRequest request) {
        String messageToResponse = "缺少必要请求头";
        String messageToLog = String.format("HTTP 请求【%s】-> %s：%s", request, messageToResponse, e.getMessage());
        return getRestDataResponseEntity(request, HttpStatus.BAD_REQUEST, messageToResponse, messageToLog);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestData<Void>> handleException(HttpMessageNotReadableException e, WebRequest request) {
        String messageToResponse = "请求体内容有误";
        String messageToLog = String.format("HTTP 请求【%s】-> %s：%s", request, messageToResponse, e.getMessage());
        return getRestDataResponseEntity(request, HttpStatus.BAD_REQUEST, messageToResponse, messageToLog);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RestData<Void>> handleException(MissingServletRequestParameterException e, WebRequest request) {
        String messageToResponse = String.format("缺少必填参数【%s】", e.getParameterName());
        String messageToLog = String.format("HTTP 请求【%s】-> %s", request, messageToResponse);
        return getRestDataResponseEntity(request, HttpStatus.BAD_REQUEST, messageToResponse, messageToLog);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestData<Void>> handleException(ConstraintViolationException e, WebRequest request) {
        List<String> errorsToLog = new ArrayList<>();
        List<String> errorsToResponse = new ArrayList<>();

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String messageToResponse = violation.getMessage();
            String messageToLog = String.format("%s.%s【拒绝值【%s】：%s】",
                    violation.getRootBeanClass().getName(),
                    violation.getPropertyPath(),
                    violation.getInvalidValue(),
                    messageToResponse);
            errorsToLog.add(messageToLog);
            errorsToResponse.add(messageToResponse);
        }

        String messageToResponse = String.join("；", errorsToResponse);
        String messageToLog = String.format("HTTP 请求【%s】-> 参数错误：%s", request, String.join("；", errorsToLog));
        return getRestDataResponseEntity(request, HttpStatus.BAD_REQUEST, messageToResponse, messageToLog);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<RestData<Void>> handleException(BindException e, WebRequest request) {
        List<String> errorsToLog = new ArrayList<>();
        List<String> errorsToResponse = new ArrayList<>();

        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String errorToResponse = fieldError.getDefaultMessage();
            String errorToLog = String.format("%s.%s【拒绝值【%s】：%s】",
                    fieldError.getObjectName(),
                    fieldError.getField(),
                    fieldError.getRejectedValue(),
                    errorToResponse);
            errorsToLog.add(errorToLog);
            errorsToResponse.add(errorToResponse);
        }

        String messageToResponse = String.join("；", errorsToResponse);
        String messageToLog = String.format("HTTP 请求【%s】-> 参数错误：%s", request, String.join("；", errorsToLog));
        return getRestDataResponseEntity(request, HttpStatus.BAD_REQUEST, messageToResponse, messageToLog);
    }

    @ExceptionHandler(AbstractBaseException.class)
    public ResponseEntity<RestData<Void>> handleException(AbstractBaseException e, WebRequest request) {
        Optional<Throwable> exceptionCauseOptional = Optional.ofNullable(e.getCause());
        String messageToResponse = e.getMessage();
        String messageToLog;
        if (exceptionCauseOptional.isEmpty()) {
            messageToLog = String.format("HTTP 请求【%s】-> %s", request, messageToResponse);
        } else {
            messageToLog = String.format("HTTP 请求【%s】-> %s：%s", request, messageToResponse, exceptionCauseOptional.get().getMessage());
        }
        return getRestDataResponseEntity(request, e.getHttpStatus(), messageToResponse, messageToLog);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<RestData<Void>> handleException(InternalServerException e, WebRequest request) {
        String messageToResponse = e.getMessage();
        String messageToLog = String.format("HTTP 请求【%s】-> 服务异常（已知）：%s", request, messageToResponse);
        return getRestDataResponseEntity(request, messageToResponse, messageToLog, e);
    }

    @ExceptionHandler(UncategorizedDataAccessException.class)
    public ResponseEntity<RestData<Void>> handleException(UncategorizedDataAccessException e, WebRequest request) {
        String messageToResponse = "数据库操作异常";
        String messageToLog = String.format("HTTP 请求【%s】-> %s", request, messageToResponse);
        return getRestDataResponseEntity(request, messageToResponse, messageToLog, e);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<RestData<Void>> handleException(Throwable e, WebRequest request) {
        if (e instanceof AccessDeniedException) {
            // 不要让全局异常处理 AccessDeniedException，而是应该由 Spring Security 处理并返回 403
            throw (AccessDeniedException) e;
        }

        String messageToResponse = "服务异常（未知）";
        String messageToLog = String.format("HTTP 请求【%s】-> %s", request, messageToResponse);
        return getRestDataResponseEntity(request, messageToResponse, messageToLog, e);
    }

    private ResponseEntity<RestData<Void>> getRestDataResponseEntity(WebRequest request,
                                                                     String messageToResponse,
                                                                     String messageToLog,
                                                                     Throwable e) {
        log.error(messageToLog, e);

        HttpStatus httpStatusToResponse = HttpStatus.INTERNAL_SERVER_ERROR;
        if (isNotJsonRequest(request)) {
            return new ResponseEntity<>(null, httpStatusToResponse);
        }
        return new ResponseEntity<>(RestDataWrapper.fail(messageToResponse), httpStatusToResponse);
    }

    private ResponseEntity<RestData<Void>> getRestDataResponseEntity(WebRequest request,
                                                                     HttpStatus httpStatusToResponse,
                                                                     String messageToResponse,
                                                                     String messageToLog) {
        log.warn(messageToLog);

        if (isNotJsonRequest(request)) {
            return new ResponseEntity<>(null, httpStatusToResponse);
        }
        return new ResponseEntity<>(RestDataWrapper.fail(messageToResponse), httpStatusToResponse);
    }

    private boolean isNotJsonRequest(WebRequest request) {
        Optional<String[]> acceptsOptional = Optional.ofNullable(request.getHeaderValues(HttpHeaders.ACCEPT));
        if (acceptsOptional.isEmpty()) {
            return true;
        }
        return Arrays.stream(acceptsOptional.get())
                .noneMatch(accept -> accept.contains(MediaType.ALL_VALUE) ||
                        accept.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE));
    }
}
