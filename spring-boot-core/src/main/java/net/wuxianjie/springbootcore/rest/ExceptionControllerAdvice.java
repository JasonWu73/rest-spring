package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.springbootcore.exception.AbstractBaseException;
import net.wuxianjie.springbootcore.exception.AbstractServerBaseException;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.util.NetUtils;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
   * 处理非正常关闭 socket 引发的错误。
   *
   * @param e   {@link ClientAbortException}
   * @param req {@link HttpServletRequest}
   */
  @ExceptionHandler(ClientAbortException.class)
  public void handleException(ClientAbortException e,
                              HttpServletRequest req) {
    String msg = StrUtil.format("非正常关闭 socket：{}", e.getMessage());

    logWarnOrError(req, msg, e, true);
  }

  /**
   * 处理因 HTTP 请求的 MIME 类型与目标 API 不匹配而导致的异常。
   *
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<ApiResult<Void>> handleException(HttpMediaTypeNotAcceptableException e,
                                                         HttpServletRequest req) {
    String mimeTypes = Optional.ofNullable(req.getHeaders(HttpHeaders.ACCEPT))
      .map(enumeration -> {
        List<String> accepts = new ArrayList<>();

        while (enumeration.hasMoreElements()) {
          String accept = enumeration.nextElement().trim();

          accepts.add(accept);
        }

        return String.join(", ", accepts);
      })
      .orElse("null");

    String respMsg = StrUtil.format("API 不支持当前请求的 MIME 类型 [{}: {}]", HttpHeaders.ACCEPT, mimeTypes);

    logWarnOrError(req, respMsg, e, true);

    return buildResponseEntity(req, HttpStatus.NOT_ACCEPTABLE, respMsg);
  }

  /**
   * 处理因 HTTP 请求的请求方法与目标 API 要求的请求方法不匹配而导致的异常。
   *
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiResult<Void>> handleException(HttpRequestMethodNotSupportedException e,
                                                         HttpServletRequest req) {
    String respMsg = StrUtil.format("API 不支持 {} 请求方法", req.getMethod());

    logWarnOrError(req, respMsg, e, true);

    return buildResponseEntity(req, HttpStatus.METHOD_NOT_ALLOWED, respMsg);
  }

  /**
   * 处理因 HTTP 请求不是 Multipart Request，但 Controller 中参数存在 {@link MultipartFile} 而导致的异常。
   *
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<ApiResult<Void>> handleException(MultipartException e,
                                                         HttpServletRequest req) {

    String respMsg = StrUtil.format("API 请求必须为 Multipart Request");

    logWarnOrError(req, respMsg, e, true);

    return buildResponseEntity(req, HttpStatus.BAD_REQUEST, respMsg);
  }

  /**
   * 处理因无法解析 HTTP 请求体内容而导致的异常。
   *
   * @param e   {@link HttpMessageNotReadableException}
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResult<Void>> handleException(HttpMessageNotReadableException e,
                                                         HttpServletRequest req) {
    String respMsg = "请求体内容不合法";

    logWarnOrError(req, respMsg, e, true);

    return buildResponseEntity(req, HttpStatus.BAD_REQUEST, respMsg);
  }

  /**
   * 处理因 HTTP 请求缺少必填参数而导致的异常。
   *
   * @param e   {@link MissingServletRequestParameterException}
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResult<Void>> handleException(MissingServletRequestParameterException e,
                                                         HttpServletRequest req) {
    String respMsg = StrUtil.format("缺少必填参数 {}", e.getParameterName());

    logWarnOrError(req, respMsg, e, true);

    return buildResponseEntity(req, HttpStatus.BAD_REQUEST, respMsg);
  }

  /**
   * 处理因 HTTP 请求参数校验（{@link Validated}）不通过而导致的异常。
   *
   * @param e   {@link ConstraintViolationException}
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResult<Void>> handleException(ConstraintViolationException e,
                                                         HttpServletRequest req) {
    List<String> logMsgList = new ArrayList<>();
    List<String> respMsgList = new ArrayList<>();

    Optional.ofNullable(e.getConstraintViolations())
      .ifPresent(violations -> violations.forEach(violation -> {
        String rspMsg = violation.getMessage();

        respMsgList.add(rspMsg);

        String queryParamPath = violation.getPropertyPath().toString();
        String queryParam = queryParamPath.contains(".") ? queryParamPath.substring(queryParamPath.indexOf(".") + 1) : queryParamPath;
        String logMsg = StrUtil.format("{} [{}={}]", rspMsg, queryParam, violation.getInvalidValue());

        logMsgList.add(logMsg);
      }));

    logParamWarn(req, logMsgList);

    return buildResponseEntity(req, HttpStatus.BAD_REQUEST, String.join("；", respMsgList));
  }

  /**
   * 处理因 HTTP 请求参数校验不通过（{@link Valid}）而导致的异常。
   *
   * @param e   {@link BindException}
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiResult<Void>> handleException(BindException e,
                                                         HttpServletRequest req) {
    List<String> logMsgList = new ArrayList<>();
    List<String> respMsgList = new ArrayList<>();

    e.getBindingResult().getFieldErrors()
      .forEach(fieldError -> {
        // 当 Controller 接收的参数类型不符合要求时，只需提示参数有误即可，而不是返回服务异常
        boolean isControllerArgTypeError = fieldError.contains(TypeMismatchException.class);
        String field = fieldError.getField();
        String respMsg;

        if (isControllerArgTypeError) {
          respMsg = StrUtil.format("{} 类型不匹配", field);
        } else {
          respMsg = fieldError.getDefaultMessage();
        }

        respMsgList.add(respMsg);

        String logMsg = StrUtil.format("{} [{}={}]", respMsg, field, fieldError.getRejectedValue());

        logMsgList.add(logMsg);
      });

    logParamWarn(req, logMsgList);

    return buildResponseEntity(req, HttpStatus.BAD_REQUEST, String.join("；", respMsgList));
  }

  /**
   * 处理 JDBC 操作异常。
   *
   * @param e   {@link UncategorizedDataAccessException}
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(UncategorizedDataAccessException.class)
  public ResponseEntity<ApiResult<Void>> handleException(UncategorizedDataAccessException e,
                                                         HttpServletRequest req) {
    String respMsg = "数据库操作异常";

    logError(req, respMsg, e);

    return buildResponseEntity(req, HttpStatus.INTERNAL_SERVER_ERROR, respMsg);
  }

  /**
   * 处理自定义异常，且不记录异常堆栈信息。
   *
   * @param e   {@link AbstractBaseException}
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(AbstractBaseException.class)
  public ResponseEntity<ApiResult<Void>> handleCustomException(AbstractBaseException e,
                                                               HttpServletRequest req) {
    String respMsg = e.getMessage();
    boolean isServerError = e instanceof AbstractServerBaseException;

    Optional.ofNullable(e.getCause())
      .ifPresentOrElse(
        cause -> logWarnOrError(req, respMsg, cause, !isServerError),
        () -> logWarnOrError(req, respMsg, !isServerError)
      );

    return buildResponseEntity(req, e.getHttpStatus(), respMsg);
  }

  /**
   * 处理所有未被特定 {@code handleException(...)} 方法捕获的异常。
   *
   * @param e   {@link Throwable}
   * @param req {@link HttpServletRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(Throwable.class)
  public ResponseEntity<ApiResult<Void>> handleAllException(Throwable e,
                                                            HttpServletRequest req) {
    // 不要处理 AccessDeniedException，否则会导致 Spring Security 无法处理 403
    if (e instanceof AccessDeniedException) {
      throw (AccessDeniedException) e;
    }

    String respMsg = "服务异常";

    logError(req, respMsg, e);

    return buildResponseEntity(req, HttpStatus.INTERNAL_SERVER_ERROR, respMsg);
  }

  private void logWarnOrError(HttpServletRequest req,
                              String respMsg,
                              boolean isWarn) {
    TokenUserDetails currentUser = getCurrentUser();
    String logMsg = StrUtil.format(
      "uri={}；client={}；accountName={}；accountId={} -> {}",
      req.getRequestURI(),
      NetUtils.getRealIpAddress(req),
      currentUser.getAccountName(),
      currentUser.getAccountId(),
      respMsg
    );

    if (isWarn) {
      log.warn(logMsg);
    } else {
      log.error(logMsg);
    }
  }

  private void logWarnOrError(HttpServletRequest req,
                              String respMsg,
                              Throwable e,
                              boolean isWarn) {
    TokenUserDetails currentUser = getCurrentUser();
    String logMsg = StrUtil.format(
      "uri={}；client={}；accountName={}；accountId={} -> {}：{}",
      req.getRequestURI(),
      NetUtils.getRealIpAddress(req),
      currentUser.getAccountName(),
      currentUser.getAccountId(),
      respMsg,
      e.getMessage()
    );

    if (isWarn) {
      log.warn(logMsg);
    } else {
      log.error(logMsg);
    }
  }

  private void logParamWarn(HttpServletRequest req, List<String> logMsgList) {
    TokenUserDetails currentUser = getCurrentUser();
    log.warn(
      "uri={}；client={}；accountName={}；accountId={} -> 参数不合法：{}",
      req.getRequestURI(),
      NetUtils.getRealIpAddress(req),
      currentUser.getAccountName(),
      currentUser.getAccountId(),
      String.join("；", logMsgList)
    );
  }

  private void logError(HttpServletRequest req, String respMsg, Throwable e) {
    TokenUserDetails currentUser = getCurrentUser();

    log.error(
      "uri={}；client={}；accountName={}；accountId={} -> {}",
      req.getRequestURI(),
      NetUtils.getRealIpAddress(req),
      currentUser.getAccountName(),
      currentUser.getAccountId(),
      respMsg,
      e
    );
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

  private ResponseEntity<ApiResult<Void>> buildResponseEntity(HttpServletRequest req,
                                                              HttpStatus httpStatus,
                                                              String respMsg) {
    if (isJsonRequest(req)) {
      return new ResponseEntity<>(ApiResultWrapper.fail(respMsg), httpStatus);
    }

    return new ResponseEntity<>(null, httpStatus);
  }

  private boolean isJsonRequest(HttpServletRequest req) {
    // 只有在 Accept 请求头中明确指定不包含 JSON 时才认为非 JSON 请求
    return Optional.ofNullable(req.getHeaders(HttpHeaders.ACCEPT))
      .map(acceptEnum -> {
        if (!acceptEnum.hasMoreElements()) {
          return true;
        }

        while (acceptEnum.hasMoreElements()) {
          String accept = acceptEnum.nextElement();
          String[] jsonArray = {MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE};

          boolean containsJson = StrUtil.containsAnyIgnoreCase(accept, jsonArray);

          if (containsJson) {
            return true;
          }
        }

        return false;
      })
      .orElse(true);
  }
}
