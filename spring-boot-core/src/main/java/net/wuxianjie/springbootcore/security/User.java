package net.wuxianjie.springbootcore.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring Security 授权注解：仅 USER 角色可访问的方法。例如：
 *
 * <pre>{@code
 * @RestController
 * public class Controller {
 *
 *     @User
 *     @RequestMapping("/test")
 *         public void test() {
 *     }
 * }}</pre>
 *
 * @author 吴仙杰
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(" +
        "T(net.wuxianjie.springbootcore.security.Role).USER.name()" +
        ")")
public @interface User {
}
