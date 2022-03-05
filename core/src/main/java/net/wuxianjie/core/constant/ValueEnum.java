package net.wuxianjie.core.constant;

/**
 * 实现该接口后，可使枚举类被 MyBatis 正确识别
 */
public interface ValueEnum {

    /**
     * 获取常量值，即存储于数据库的值
     *
     * @return 枚举的常量值
     */
    int value();
}
