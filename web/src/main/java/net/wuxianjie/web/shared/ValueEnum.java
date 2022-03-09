package net.wuxianjie.web.shared;

/**
 * 实现该接口后，可使枚举值被 MyBatis 正确识别，映射到数据库 int 数据类型。
 */
public interface ValueEnum {

    int value();
}
