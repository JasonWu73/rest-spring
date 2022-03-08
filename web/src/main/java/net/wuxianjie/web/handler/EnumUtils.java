package net.wuxianjie.web.handler;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnumUtils {

    /**
     * 将 Integer 值解析为枚举值
     *
     * @param val Integer 值
     * @return 枚举值，若无法解析则返回 null
     */
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
