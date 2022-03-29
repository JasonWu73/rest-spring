package net.wuxianjie.springbootcore.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

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
}