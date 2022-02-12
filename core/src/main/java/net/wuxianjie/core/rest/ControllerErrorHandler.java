package net.wuxianjie.core.rest;

import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.exception.DataConflictException;
import net.wuxianjie.core.exception.InternalServerException;
import net.wuxianjie.core.exception.TokenAuthenticationException;
import net.wuxianjie.core.model.RestResponse;
import net.wuxianjie.core.util.ResponseResultWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

/**
 * Spring全局控制器异常处理
 *
 * <p>{@code @ControllerAdvice}与{@code @ExceptionHandler}配合，只能处理控制器抛出的异常，此时请求已进入控制器</p>
 *
 * <p>实现{@code ErrorController}接口，则可以处理所有异常，包括未进入控制器的错误，比如404</p>
 *
 * <p>如果{@code @ControllerAdvice}与{@code ErrorController}实现类同时存在，
 * 则{@code @ControllerAdvice}方式处理控制器抛出的异常，
 * {@code ErrorController}实现类处理未进入控制器的异常</p>
 *
 * @author 吴仙杰
 */
@Slf4j
@ControllerAdvice
public class ControllerErrorHandler {

  private static final String HTTP_HEADER_ACCEPT = "accept";

  /**
   * 处理当客户端的请求内容类型不被支持时的异常
   *
   * @param e 当请求的内容类型不支持时的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(HttpMediaTypeException.class)
  public ResponseEntity<RestResponse<Void>> handleMediaType(final HttpMediaTypeException e, final WebRequest request) {
    final String[] accepts = request.getHeaderValues(HTTP_HEADER_ACCEPT);

    log.warn("该API不支持当前所请求的内容类型（{}）：{}",
        accepts == null ? "..." : Arrays.asList(accepts), e.getMessage());
    return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
  }

  /**
   * 处理当请求方法不支持时的异常
   *
   * @param e 当请求方法不支持时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<RestResponse<Void>> handleMethodNotSupported(final HttpRequestMethodNotSupportedException e, final WebRequest request) {
    log.warn("该API不支持当前HTTP方法：{}", e.getMessage());

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.METHOD_NOT_ALLOWED);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail("该API不支持当前HTTP方法"), HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * 处理当请求的请求头中不存在{@code @RequestMapping}参数中期望的请求头时抛出的异常
   *
   * @param e 当请求缺少必要请求头时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<RestResponse<Void>> handleMissingRequestHeader(final MissingRequestHeaderException e, final WebRequest request) {
    log.warn("缺少必要的请求头：{}", e.getMessage());

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail("缺少必要的请求头"), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理当Controller方法需要读取请求体，但客户端未传入或传入数据不匹配时的异常
   *
   * @param e 当请求体未传入或传入数据不匹配时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<RestResponse<Void>> handleMessageNotReadable(final HttpMessageNotReadableException e, final WebRequest request) {
    log.warn("当前请求体内容有误：{}", e.getMessage());

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail("当前请求体内容有误"), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理当请求缺少必要URL参数时的异常
   *
   * @param e 当请求缺少必要URL参数时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<RestResponse<Void>> handleMissingParameter(final MissingServletRequestParameterException e, final WebRequest request) {
    final String parameterName = e.getParameterName();
    final String parameterType = e.getParameterType();
    log.warn("缺少必填参数：{}，类型为{}", parameterName, parameterType);

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(ResponseResultWrapper
        .fail(String.format("%s是必填参数", parameterName)), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理当请求（JSON提交）参数校验（{@code @Valid}）不通过时抛出的异常
   *
   * @param e 当请求参数校验不通过时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<RestResponse<Void>> handleArgumentNotValid(final MethodArgumentNotValidException e, final WebRequest request) {
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

    log.warn("参数错误：{}", String.join("; ", logErrors));

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail(String.join("; ", responseErrors)), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理当请求参数（表单，URL）校验不通过时抛出的异常
   *
   * @param e 当请求参数校验不通过时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<RestResponse<Void>> handleConstraintViolation(final ConstraintViolationException e, final WebRequest request) {
    final List<String> logErrors = new ArrayList<>();
    final List<String> responseErrors = new ArrayList<>();

    for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
      final String error = violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage();

      logErrors.add(error);

      responseErrors.add(violation.getMessage());
    }

    log.warn("参数错误：{}", String.join("; ", logErrors));

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail(String.join("；", responseErrors)), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理当请求身份认证不通过时抛出的异常
   *
   * @param e 当请求身份认证不通过时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(TokenAuthenticationException.class)
  public ResponseEntity<RestResponse<Void>> handleAuthentication(final TokenAuthenticationException e, final WebRequest request) {
    if (e.getCause() == null) {
      log.warn("{}", e.getMessage());
    } else {
      log.warn("{}：{}", e.getMessage(), e.getCause().getMessage());
    }

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail(e.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  /**
   * 处理当请求有误而导致服务不可用时抛出的异常
   *
   * @param e 请求有误而导致服务不可用时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<RestResponse<Void>> handleBadRequest(final BadRequestException e, final WebRequest request) {
    log.warn("客户端请求有误：{}", e.getMessage());

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail(e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  /**
   * 处理因数据已存在而导致操作冲突的异常
   *
   * @param e 因数据已存在而导致操作冲突的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<RestResponse<Void>> handleDataConflict(final DataConflictException e, final WebRequest request) {
    log.warn("数据冲突：{}", e.getMessage());

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.CONFLICT);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail(e.getMessage()), HttpStatus.CONFLICT);
  }

  /**
   * 处理当请求有误而导致服务不可用时抛出的异常
   *
   * @param e 请求有误而导致服务不可用时抛出的异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<RestResponse<Void>> handleInternalServer(final InternalServerException e, final WebRequest request) {
    log.warn("服务器端有误：{}", e.getMessage());

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(ResponseResultWrapper.fail(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * 处理所有经过控制器抛出的异常
   *
   * @param e 异常
   * @return 包含HTTP状态码的响应体
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity<RestResponse<Void>> handleThrowable(final Throwable e, final WebRequest request) {
    if (e instanceof AccessDeniedException) {
      // 不要让全局异常处理阻止了Spring Security的403处理
      throw (AccessDeniedException) e;
    }

    log.error("默认异常处理", e);

    if (shouldNotJsonResponse(request)) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    final String error = e.getMessage() == null ? "null空指针异常" : e.getMessage();
    return new ResponseEntity<>(ResponseResultWrapper.fail(error), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private boolean shouldNotJsonResponse(WebRequest request) {
    final String[] accepts = request.getHeaderValues(HTTP_HEADER_ACCEPT);

    if (accepts == null) {
      return true;
    }

    return Arrays.stream(accepts)
        .noneMatch(x ->
            x.contains("*/*") || x.toLowerCase().contains("application/json"));
  }
}
