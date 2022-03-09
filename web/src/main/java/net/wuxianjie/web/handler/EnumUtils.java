package net.wuxianjie.web.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumUtils {

    @Nullable
    public static <E extends Enum<?> & ValueEnum> E resolve(Class<E> enumClass, Integer val) {
        if (enumClass == null || val == null) {
            return null;
        }

        E[] enumConstants = enumClass.getEnumConstants();

        for (E e : enumConstants) {
            if (e.value() == val) {
                return e;
            }
        }

        return null;
    }
}
