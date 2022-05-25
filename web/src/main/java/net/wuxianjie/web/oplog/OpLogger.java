package net.wuxianjie.web.oplog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注需要记录操作日志的方法。
 *
 * @author 吴仙杰
 * @see OpLogAspect
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OpLogger {

  /**
   * 操作描述。
   *
   * @return 操作描述
   */
  String value();
}
