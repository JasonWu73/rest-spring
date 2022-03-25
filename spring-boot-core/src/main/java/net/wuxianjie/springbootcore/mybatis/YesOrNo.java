package net.wuxianjie.springbootcore.mybatis;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Optional;

/**
 * 适用于仅表示“是/否”、“对/错”、“有/无”等只有两种状态的枚举常量。
 *
 * @author 吴仙杰
 */
@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
@ToString
public enum YesOrNo implements ValueEnum {

    /**
     * 否。
     */
    NO(0),

    /**
     * 是。
     */
    YES(1);

    private static final YesOrNo[] VALUES;

    static {
        VALUES = values();
    }

    @JsonValue
    private final int value;

    /**
     * 将整数值解析为枚举常量。
     *
     * @param value 整数值
     * @return 整数值所对应的枚举常量
     */
    public static Optional<YesOrNo> resolve(int value) {
        for (YesOrNo enumConstant : VALUES) {
            if (value == enumConstant.value) {
                return Optional.of(enumConstant);
            }
        }

        return Optional.empty();
    }
}
