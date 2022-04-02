package net.wuxianjie.springbootcore.mybatis;

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
 * 全局配置（application.yml）<br>
 * {@code mybatis.type-handlers-package: net.wuxianjie.springbootcore.mybatis}
 * </p>
 *
 * @author 吴仙杰
 * @see ValueEnum
 */
@NoArgsConstructor
public class EnumTypeHandler<E extends Enum<?> & ValueEnum> extends BaseTypeHandler<ValueEnum> {

    private Class<E> enumType;

    public EnumTypeHandler(final Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("enumType 参数不能为 null");
        }

        this.enumType = enumType;
    }

    @Override
    public void setNonNullParameter(final PreparedStatement ps,
                                    final int i,
                                    final ValueEnum parameter,
                                    final JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.value());
    }

    @Override
    public ValueEnum getNullableResult(final ResultSet rs,
                                       final String columnName) throws SQLException {
        return toNullableValueNum(enumType, rs.getInt(columnName));
    }

    @Override
    public ValueEnum getNullableResult(final ResultSet rs,
                                       final int columnIndex) throws SQLException {
        return toNullableValueNum(enumType, rs.getInt(columnIndex));
    }

    @Override
    public ValueEnum getNullableResult(final CallableStatement cs,
                                       final int columnIndex) throws SQLException {
        return toNullableValueNum(enumType, cs.getInt(columnIndex));
    }

    private E toNullableValueNum(final Class<E> enumClass,
                                 final Integer value) {
        if (enumClass == null || value == null) return null;

        E[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) return null;

        for (final E e : enumConstants) {
            if (value == e.value()) {
                return e;
            }
        }

        return null;
    }
}
