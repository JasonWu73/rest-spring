package net.wuxianjie.core.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标识普通用户可访问的权限认证注解，可避免字符串硬编码（如{@code @PreAuthorize("hasRole('USER')")}）
 *
 * @author 吴仙杰
 * @see <a href="https://stackoverflow.com/questions/19303584/spring-security-preauthorization-pass-enums-in-directly">java - Spring Security @PreAuthorization pass enums in directly - Stack Overflow</a>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(net.wuxianjie.core.constant.AuthRole).USER.value().toUpperCase())")
public @interface User {

}
