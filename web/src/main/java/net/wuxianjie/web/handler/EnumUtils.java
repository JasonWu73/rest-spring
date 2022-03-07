package net.wuxianjie.web.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumUtils {

    /**
     * 从指定值解析为枚举值
     *
     * @param val 值
     * @return 若没有找到则为 {@code null}
     */
    public static <E extends Enum<?> & ValueEnum> E resolve(
            @NonNull final Class<E> enumClass,
            final Integer val
    ) {
        if (val == null) {
            return null;
        }

        final E[] enumConstants = enumClass.getEnumConstants();

        for (final E e : enumConstants) {
            if (e.value() == val) {
                return e;
            }
        }

        return null;
    }
}
