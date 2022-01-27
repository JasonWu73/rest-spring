package net.wuxianjie.core.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.model.ResponseResult;
import net.wuxianjie.core.util.ResponseResultWrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理 Spring Security 401
 *
 * @author 吴仙杰
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationFailHandler implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  @Override
  public void commence(final HttpServletRequest request,final HttpServletResponse response, final AuthenticationException authException) throws IOException {

    final ResponseResult<Void> result = ResponseResultWrappers.fail("身份认证失败");

    //noinspection deprecation
    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());

    response.getWriter().write(objectMapper.writeValueAsString(result));
  }
}
