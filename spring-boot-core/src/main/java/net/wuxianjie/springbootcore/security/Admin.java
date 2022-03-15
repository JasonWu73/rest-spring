package net.wuxianjie.springbootcore.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring Security 授权注解 - 仅 admin 角色可访问的方法。如：
 *
 * <pre>{@code
 * @RestController
 * public class Controller {
 *
 *     @Admin
 *     @RequestMapping("/test")
 *         public void test() {
 *     }
 * }}</pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole("
    + "T(net.wuxianjie.springbootcore.security.Role).ADMIN.value().toUpperCase())")
public @interface Admin {}
