package net.wuxianjie.springbootcore.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring Security 授权注解：只要拥有 USER 或 ADMIN 任意一种角色就可访问的方法。例如：
 *
 * <pre>{@code
 * @RestController
 * public class Controller {
 *
 *     @UserOrAdmin
 *     @RequestMapping("/test")
 *         public void test() {
 *     }
 * }}</pre>
 *
 * @author 吴仙杰
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole(" +
        "T(net.wuxianjie.springbootcore.security.Role).USER.name(), " +
        "T(net.wuxianjie.springbootcore.security.Role).ADMIN.name()" +
        ")")
public @interface UserOrAdmin {
}
