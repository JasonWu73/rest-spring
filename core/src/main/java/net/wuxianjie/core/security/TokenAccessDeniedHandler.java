package net.wuxianjie.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.constant.CommonValues;
import net.wuxianjie.core.model.RestResponse;
import net.wuxianjie.core.util.ResponseResultWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理Spring Security 403，即身份认证通过，而权限校验不通过。
 *
 * <p>注意：需要由Spring Security自己处理`AccessDeniedException`异常，即若有全局异常处理，则不生效</p>
 *
 * @author 吴仙杰
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAccessDeniedHandler implements AccessDeniedHandler {
  private final ObjectMapper objectMapper;

  @Override
  public void handle(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final AccessDeniedException accessDeniedException) throws IOException {
    log.warn("权限认证失败：{}", accessDeniedException.getMessage());

    final RestResponse<Void> result = ResponseResultWrapper.fail("无访问权限");

    response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());

    response.getWriter().write(objectMapper.writeValueAsString(result));
  }
}
