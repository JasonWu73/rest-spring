package net.wuxianjie.core.validator.impl;

import lombok.extern.slf4j.Slf4j;
import net.wuxianjie.core.validator.EnumValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link EnumValidator} 校验注解的业务逻辑实现类
 */
@Slf4j
public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Object> {

    private List<Object> values;
    private boolean isPassed = false;

    @Override
    public void initialize(final EnumValidator constraintAnnotation) {
        values = new ArrayList<>();
        final Class<? extends Enum<?>> enumClass = constraintAnnotation.value();
        final Enum<?>[] enumConstants = enumClass.getEnumConstants();

        for (final Enum<?> enumValue : enumConstants) {
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
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        return isPassed || value == null || values.contains(value);
    }
}
