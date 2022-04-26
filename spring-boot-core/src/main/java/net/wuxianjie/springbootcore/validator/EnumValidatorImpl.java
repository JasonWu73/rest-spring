package net.wuxianjie.springbootcore.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link EnumValidator} 枚举值校验注解业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Slf4j
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Object> {

  private boolean isPassed = false;
  private List<Object> values;

  @Override
  public void initialize(EnumValidator constraintAnnotation) {
    values = new ArrayList<>();
    Class<? extends Enum<?>> enumClass = constraintAnnotation.value();

    Optional.of(enumClass.getEnumConstants())
      .ifPresent(enums -> {
        String className = enumClass.getName();

        for (Enum<?> anEnum : enums) {
          try {
            Method method = anEnum.getClass().getDeclaredMethod("value");
            method.setAccessible(true);

            values.add(method.invoke(anEnum));
          } catch (NoSuchMethodException e) {
            log.warn("{} 不存在 value() 方法，故无法校验枚举值", className);

            isPassed = true;
            break;
          } catch (InvocationTargetException | IllegalAccessException e) {
            log.warn("{}.value() 方法执行出错，故无法校验枚举值", className);

            isPassed = true;
            break;
          }
        }
      });
  }

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    return isPassed || value == null || values.contains(value);
  }
}
