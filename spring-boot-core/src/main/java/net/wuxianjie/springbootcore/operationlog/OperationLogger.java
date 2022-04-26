package net.wuxianjie.springbootcore.operationlog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志记录器注解。
 * <p>
 * 将 {@link OperationLogger} 用于方法后，只要该方法正确执行后，会自动调用 {@link OperationLogService#saveLog(OperationLogData)} 进行处理。
 * </p>
 *
 * @author 吴仙杰
 * @see OperationLogAspect
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLogger {

  /**
   * 操作描述。
   *
   * @return 操作描述
   */
  String value();
}
