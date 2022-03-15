package net.wuxianjie.springbootcore.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 枚举值校验注解，例如：
 *
 * <pre>{@code
 * @Getter
 * @ToString
 * @RequiredArgsConstructor
 * @Accessors(fluent = true)
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
 *   public test(@RequestBody @Validated Query query) {
 *   }
 *
 *   private static class Query {
 *
 *     @EnumValidator(message = "类型错误", value = Type.class)
 *     private Integer type;
 *   }
 * }}</pre>
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EnumValidator.List.class)
@Constraint(validatedBy = EnumValidatorImpl.class)
public @interface EnumValidator {

  Class<? extends Enum<?>> value();

  String message() default "{com.qgs.trial.validator.EnumValidator.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {

    EnumValidator[] value();
  }
}
