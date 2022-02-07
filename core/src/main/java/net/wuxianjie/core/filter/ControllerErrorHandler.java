package net.wuxianjie.core.filter;

import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.InternalServerException;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.model.dto.RestDto;
import net.wuxianjie.core.util.ResponseResultWrappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spring 全局控制器异常处理
 *
 * <p>{@code @ControllerAdvice} 与 {@code @ExceptionHandler} 配合, 只能处理控制器抛出的异常, 此时请求已进入控制器</p>
 *
 * <p>实现 {@code ErrorController} 接口, 则可以处理所有异常, 包括未进入控制器的错误, 比如404</p>
 *
 * <p>如果 {@code @ControllerAdvice} 与 {@code ErrorController} 实现类同时存在,
 * 则 {@code @ControllerAdvice} 方式处理控制器抛出的异常,
 * {@code ErrorController} 实现类处理未进入控制器的异常</p>
 *
 * @author 吴仙杰
 */
@Slf4j
@ControllerAdvice
public class ControllerErrorHandler {

  /**
   * 处理当客户端 POST, PUT 或 PATCH 的请求内容类型不被支持时的异常
   *
   * @param e 当请求的内容类型不支持时的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<RestDto<Void>> handleMimeNotSupported(final HttpMediaTypeNotSupportedException e) {
    log.warn("请求的内容类型不支持: {}", e.getMessage());
    return new ResponseEntity<>(ResponseResultWrappers.fail(e.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * 处理当请求方法不支持时的异常
   *
   * @param e 当请求方法不支持时抛出的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<RestDto<Void>> handleMethodNotSupported(final HttpRequestMethodNotSupportedException e) {
    log.warn("请求的方法不支持: {}", e.getMessage());
    return new ResponseEntity<>(ResponseResultWrappers.fail(e.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * 处理当请求的请求头中不存在 {@code @RequestMapping} 参数中期望的请求头时抛出的异常
   *
   * @param e 当请求缺少必要请求头时抛出的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<RestDto<Void>> handleMissingRequestHeader(final MissingRequestHeaderException e) {
    log.warn("缺少必要的请求头: {}", e.getMessage());
    return new ResponseEntity<>(ResponseResultWrappers.fail("缺少必要的请求头"), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理当请求 (JSON 提交) 参数校验 ({@code @Valid}) 不通过时抛出的异常
   *
   * @param e 当请求参数校验不通过时抛出的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<RestDto<Void>> handleArgumentNotValid(final MethodArgumentNotValidException e) {
    final List<String> logErrors = new ArrayList<>();
    final List<String> responseErrors = new ArrayList<>();

    final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

    for (FieldError error : fieldErrors) {
      final String message = error.getDefaultMessage();

      logErrors.add(error.getField() + ": " + message);
      responseErrors.add(message);
    }

    final List<ObjectError> globalErrors = e.getBindingResult().getGlobalErrors();

    for (ObjectError error : globalErrors) {
      final String message = error.getDefaultMessage();

      logErrors.add(error.getObjectName() + ": " + message);
      responseErrors.add(message);
    }

    log.warn("参数错误: {}", String.join("; ", logErrors));

    return new ResponseEntity<>(ResponseResultWrappers.fail(String.join("; ", responseErrors)), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理当请求参数 (表单, URL) 校验不通过时抛出的异常
   *
   * @param e 当请求参数校验不通过时抛出的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<RestDto<Void>> handleConstraintViolation(final ConstraintViolationException e) {
    final List<String> logErrors = new ArrayList<>();
    final List<String> responseErrors = new ArrayList<>();

    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      final String error = violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage();

      logErrors.add(error);

      responseErrors.add(violation.getMessage());
    }

    log.warn("参数错误: {}", String.join("; ", logErrors));

    return new ResponseEntity<>(ResponseResultWrappers.fail(String.join("; ", responseErrors)), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理 Spring Security 身份认证过后但没有访问权限时抛出的异常
   *
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<RestDto<Void>> handleAccessDeniedHandler() {
    return new ResponseEntity<>(ResponseResultWrappers.fail("无访问权限"), HttpStatus.FORBIDDEN);
  }

  /**
   * 处理当请求身份认证不通过时抛出的异常
   *
   * @param e 当请求身份认证不通过时抛出的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(TokenAuthenticationException.class)
  public ResponseEntity<RestDto<Void>> handleAuthentication(final TokenAuthenticationException e) {
    log.warn("身份认证失败: {}", e.getMessage());
    return new ResponseEntity<>(ResponseResultWrappers.fail(e.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  /**
   * 处理当请求有误而导致服务不可用时抛出的异常
   *
   * @param e 请求有误而导致服务不可用时抛出的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<RestDto<Void>> handleBadRequest(final BadRequestException e) {
    log.warn("客户端请求有误: {}", e.getMessage());
    return new ResponseEntity<>(ResponseResultWrappers.fail(e.getMessage()), HttpStatus.BAD_REQUEST);
  }
  /**
   * 处理当请求有误而导致服务不可用时抛出的异常
   *
   * @param e 请求有误而导致服务不可用时抛出的异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<RestDto<Void>> handleInternalServer(final InternalServerException e) {
    log.warn("服务器端有误: {}", e.getMessage());
    return new ResponseEntity<>(ResponseResultWrappers.fail(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * 处理所有经过控制器抛出的异常
   *
   * @param e 异常
   * @return 包含 HTTP 状态码的响应体
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity<RestDto<Void>> handleThrowable(final Throwable e) {
    log.error("默认异常处理", e);
    final String error = e.getMessage() == null ? "无法定位异常详细信息" : e.getMessage();
    return new ResponseEntity<>(ResponseResultWrappers.fail(error), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
