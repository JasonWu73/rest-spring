package net.wuxianjie.springbootcore.shared.util;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.shared.exception.InternalException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 网络工具类。
 *
 * @author 吴仙杰
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NetUtils {

    /**
     * 获取客户端请求的真实 IP 地址。
     * <p>
     * 可能会返回形如 {@code 231.23.45.65, 10.20.10.33, 10.20.20.34} 的字符串，分别代表：客户端 IP，负载均衡服务器，反向代理服务器。
     * </p>
     *
     * @return 客户端 IP
     */
    public static String getRealIpAddress(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-FORWARDED-FOR"))
                .map(StrUtil::trimToNull)
                .orElse(request.getRemoteAddr());
    }

    /**
     * 获取 Servlet 环境中当前线程中的请求对象。
     *
     * @return {@link Optional<HttpServletRequest>}
     */
    public static Optional<HttpServletRequest> getRequest() throws InternalException {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(reqAttr -> (HttpServletRequest) reqAttr.resolveReference(RequestAttributes.REFERENCE_REQUEST));
    }
}
