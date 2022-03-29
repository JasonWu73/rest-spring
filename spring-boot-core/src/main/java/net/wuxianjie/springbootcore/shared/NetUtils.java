package net.wuxianjie.springbootcore.shared;

import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
     * 获取客户端 IP 地址。
     * <p>
     * 可能会返回形如 {@code 231.23.45.65, 10.20.10.33, 10.20.20.34} 的字符串，分别代表：客户端 IP，负载均衡服务器，反向代理服务器。
     * </p>
     *
     * @return 客户端 IP
     */
    public static String getClientIp(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-FORWARDED-FOR"))
                .map(StrUtil::trimToNull)
                .orElse(request.getRemoteAddr());
    }
}
