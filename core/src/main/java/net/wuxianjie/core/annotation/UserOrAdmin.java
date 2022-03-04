package net.wuxianjie.core.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring Security 权限注解 - user 或 admin 角色都可访问的方法。如：
 *
 * <pre>{@code
 * @RestController
 * public class Controller {
 *
 *     @UserOrAdmin
 *     @RequestMapping("/test")
 *         public void test() {
 *     }
 * }
 * } </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole(" +
        "T(net.wuxianjie.core.constant.Role).USER.value().toUpperCase(), " +
        "T(net.wuxianjie.core.constant.Role).ADMIN.value().toUpperCase())"
)
public @interface UserOrAdmin {
}
