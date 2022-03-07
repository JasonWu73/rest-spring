package net.wuxianjie.web.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 处理枚举类的映射规则
 */
public class EnumTypeHandler<E extends Enum<?> & ValueEnum> extends
        BaseTypeHandler<ValueEnum> {

    private Class<E> enumType;

    public EnumTypeHandler() {
    }

    public EnumTypeHandler(final Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("enumType 参数不能为 null");
        }

        this.enumType = enumType;
    }

    @Override
    public void setNonNullParameter(
            final PreparedStatement ps,
            final int i,
            final ValueEnum parameter,
            final JdbcType jdbcType
    ) throws SQLException {
        ps.setInt(i, parameter.value());
    }

    @Override
    public ValueEnum getNullableResult(
            final ResultSet rs,
            final String columnName
    ) throws SQLException {
        return EnumUtils.resolve(enumType, rs.getInt(columnName));
    }

    @Override
    public ValueEnum getNullableResult(
            final ResultSet rs,
            final int columnIndex
    ) throws SQLException {
        return EnumUtils.resolve(enumType, rs.getInt(columnIndex));
    }

    @Override
    public ValueEnum getNullableResult(
            final CallableStatement cs,
            final int columnIndex
    ) throws SQLException {
        return EnumUtils.resolve(enumType, cs.getInt(columnIndex));
    }
}
