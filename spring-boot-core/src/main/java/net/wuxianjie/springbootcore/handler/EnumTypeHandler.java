package net.wuxianjie.springbootcore.handler;

import lombok.NoArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 类型处理器：映射 Java 枚举常量与数据库 INT 数据类型。
 * <p>
 * 全局配置（application.yml）：<br>
 * {@code mybatis.type-handlers-package: net.wuxianjie.springbootcore.handler}
 * </p>
 *
 * @author 吴仙杰
 * @see ValueEnum
 */
@NoArgsConstructor
public class EnumTypeHandler<E extends Enum<?> & ValueEnum>
        extends BaseTypeHandler<ValueEnum> {

    private Class<E> enumType;

    public EnumTypeHandler(Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("enumType 参数不能为 null");
        }

        this.enumType = enumType;
    }

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            ValueEnum parameter,
            JdbcType jdbcType
    ) throws SQLException {
        ps.setInt(i, parameter.value());
    }

    @Override
    public ValueEnum getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return toNullableValueNum(enumType, rs.getInt(columnName));
    }

    @Override
    public ValueEnum getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return toNullableValueNum(enumType, rs.getInt(columnIndex));
    }

    @Override
    public ValueEnum getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        return toNullableValueNum(enumType, cs.getInt(columnIndex));
    }


    private E toNullableValueNum(Class<E> enumClass, Integer value) {
        if (enumClass == null || value == null) {
            return null;
        }

        E[] enumConstants = enumClass.getEnumConstants();

        if (enumConstants == null) {
            return null;
        }

        for (E enumConstant : enumConstants) {
            if (enumConstant.value() == value) {
                return enumConstant;
            }
        }

        return null;
    }
}
