package net.wuxianjie.springbootcore.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.handler.ValueEnum;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumUtils {

  public static <E extends Enum<?> & ValueEnum> Optional<E> resolve(
      Class<E> enumClass,
      Integer val) {
    if (enumClass == null || val == null) {
      return Optional.empty();
    }

    final E[] enumConstants = enumClass.getEnumConstants();

    for (E e : enumConstants) {
      if (e.value() == val) {
        return Optional.of(e);
      }
    }

    return Optional.empty();
  }
}
