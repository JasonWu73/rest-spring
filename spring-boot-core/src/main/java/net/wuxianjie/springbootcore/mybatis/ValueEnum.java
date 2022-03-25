package net.wuxianjie.springbootcore.mybatis;

/**
 * 实现该接口后，可映射 Java 枚举常量与数据库 INT 数据类型。
 *
 * @author 吴仙杰
 * @see EnumTypeHandler
 */
public interface ValueEnum {

    /**
     * 获取枚举常量的整数值。
     *
     * @return 枚举常量对应的整数值
     */
    int value();
}
