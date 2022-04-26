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
 *     ME(1);
 *
 *     @JsonValue
 *     private final int value;
 * }
 *
 * public class Controller {
 *
 *     public test(@RequestBody @Validated final Query query) {
 *     }
 *
 *     private static class Query {
 *
 *         @EnumValidator(message = "类型不合法", value = Type.class)
 *         private Integer type;
 *     }
 * }}</pre>
 *
 * @author 吴仙杰
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorImpl.class)
@Repeatable(EnumValidator.List.class)
public @interface EnumValidator {

  Class<? extends Enum<?>> value();

  String message() default "枚举类型值不合法";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface List {

    EnumValidator[] value();
  }
}
