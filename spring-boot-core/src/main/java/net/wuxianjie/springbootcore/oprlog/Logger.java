package net.wuxianjie.springbootcore.oprlog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志记录器注解。
 *
 * @author 吴仙杰
 * @see LogAspect
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Logger {

    /**
     * 操作描述。
     *
     * @return 操作描述
     */
    String value();
}
