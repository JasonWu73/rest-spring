package net.wuxianjie.core.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现 {@link EnumValidator} 枚举值校验注解的业务逻辑。
 */
@Slf4j
public class EnumValidatorImpl
  implements ConstraintValidator<EnumValidator, Object> {

  private boolean isPassed = false;
  private List<Object> values;

  @Override
  public void initialize(EnumValidator constraintAnnotation) {
    values = new ArrayList<>();
    final Class<? extends Enum<?>> enumClass = constraintAnnotation.value();
    final Enum<?>[] enumConstants = enumClass.getEnumConstants();

    for (Enum<?> enumValue : enumConstants) {
      final Method valueMethod;

      try {
        valueMethod = enumValue.getClass().getDeclaredMethod("value");
      } catch (NoSuchMethodException ignore) {
        log.warn("枚举类中不存在 value() 方法，故无法校验枚举值");

        isPassed = true;

        break;
      }

      final Object value;

      try {
        valueMethod.setAccessible(true);
        value = valueMethod.invoke(enumValue);

        values.add(value);
      } catch (IllegalAccessException | InvocationTargetException ignore) {
        log.warn("无法正确执行枚举类中的 value() 方法，故无法校验枚举值");

        isPassed = true;

        break;
      }
    }
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    return isPassed || value == null || values.contains(value);
  }
}
