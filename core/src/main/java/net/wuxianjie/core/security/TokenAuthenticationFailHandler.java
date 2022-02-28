package net.wuxianjie.core.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.constant.CommonValues;
import net.wuxianjie.core.model.RestResponse;
import net.wuxianjie.core.util.ResponseResultWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理Spring Security 401，即身份认证不通过
 *
 * @author 吴仙杰
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationFailHandler implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(final HttpServletRequest request,final HttpServletResponse response, final AuthenticationException authException) throws IOException {
    log.warn("身份认证失败：{}", authException.getMessage());

    final RestResponse<Void> result = ResponseResultWrapper.fail("身份认证失败");

    response.setContentType(CommonValues.APPLICATION_JSON_UTF8_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());

    response.getWriter().write(objectMapper.writeValueAsString(result));
  }
}
