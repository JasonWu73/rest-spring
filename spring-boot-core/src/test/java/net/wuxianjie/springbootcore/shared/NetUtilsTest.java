package net.wuxianjie.springbootcore.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author 吴仙杰
 */
class NetUtilsTest {

    @Test
    @DisplayName("当客户端没有代理服务器时获取客户端请求 IP")
    void canGetClientIp() {
        // given
        String ip = "192.168.1.98";
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRemoteAddr(ip);

        // when
        String actual = NetUtils.getClientIp(request);

        // then
        assertThat(actual).isEqualTo(ip);
    }

    @Test
    @DisplayName("当客户端存在代理服务器时获取客户端请求 IP")
    void canGetRealClientIp() {
        // given
        String ip = "192.168.1.98";
        String proxyIp = "192.168.129.89";
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setRemoteAddr(ip);
        request.addHeader("X-FORWARDED-FOR", proxyIp);

        // when
        String actual = NetUtils.getClientIp(request);

        // then
        assertThat(actual).isEqualTo(proxyIp);
    }

    @Test
    @DisplayName("获取请求对象")
    void canGetRequest() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // when
        Optional<HttpServletRequest> actual = NetUtils.getRequest();

        // then
        assertThat(actual.isPresent()).isTrue();
    }

    @Test
    @DisplayName("无法请求对象")
    void canNotGetRequest() {
        // given
        // when
        Optional<HttpServletRequest> actual = NetUtils.getRequest();

        // then
        assertThat(actual.isEmpty()).isTrue();
    }
}