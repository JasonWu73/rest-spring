package net.wuxianjie.springbootcore.handler;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 * 适用于任何仅有是或否的枚举值。
 *
 * @author 吴仙杰
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public enum YesOrNo implements ValueEnum {

    NO(0),

    YES(1);

    private static final YesOrNo[] VALUES;

    static {
        VALUES = values();
    }

    @JsonValue
    private final int value;

    public static Optional<YesOrNo> resolve(int value) {
        for (YesOrNo yesOrNo : VALUES) {
            if (value == yesOrNo.value) {
                return Optional.of(yesOrNo);
            }
        }

        return Optional.empty();
    }
}
