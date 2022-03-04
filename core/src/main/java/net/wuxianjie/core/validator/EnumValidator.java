package net.wuxianjie.core.validator;

import net.wuxianjie.core.validator.impl.EnumValidatorImpl;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 可用于对枚举值的校验，如：
 * <pre>{@code
 * @RequiredArgsConstructor
 * @Getter
 * @Accessors(fluent = true)
 * @ToString
 * public enum Type {
 *
 *   ME(1);
 *
 *   @JsonValue
 *   private final int value;
 * }
 *
 * public class Controller {
 *
 *   public test(@RequestBody @Validated final Query query) {
 *
 *   }
 *
 *   static class Query {
 *
 *    @EnumValidator(message = "类型错误", value = Type.class)
 *    private Integer type;
 *   }
 * }
 * }</pre>
 */
@Target({
  ElementType.METHOD,
  ElementType.FIELD,
  ElementType.ANNOTATION_TYPE,
  ElementType.CONSTRUCTOR,
  ElementType.PARAMETER,
  ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorImpl.class)
@Repeatable(EnumValidator.List.class)
public @interface EnumValidator {

  Class<? extends Enum<?>> value();

  String message() default "{com.qgs.trial.validator.EnumValidator.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.ANNOTATION_TYPE,
    ElementType.CONSTRUCTOR,
    ElementType.PARAMETER,
    ElementType.TYPE_USE
  })
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {

    EnumValidator[] value();
  }
}
