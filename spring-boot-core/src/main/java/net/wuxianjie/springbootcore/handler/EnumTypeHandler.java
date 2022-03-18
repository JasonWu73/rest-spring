package net.wuxianjie.springbootcore.handler;

import lombok.NoArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * MyBatis 处理数据库 int 类型与枚举值的映射规则。
 *
 * <p>全局配置（application.yml）：</p>
 *
 * <p>{@code mybatis.type-handlers-package: net.wuxianjie.springbootcore.handler}</p>
 *
 * @author 吴仙杰
 * @see ValueEnum
 */
@NoArgsConstructor
public class EnumTypeHandler<E extends Enum<?> & ValueEnum> extends BaseTypeHandler<ValueEnum> {

    private Class<E> enumType;

    public EnumTypeHandler(Class<E> enumType) {
        if (enumType == null) {
            throw new IllegalArgumentException("enumType 参数不能为 null");
        }

        this.enumType = enumType;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ValueEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.value());
    }

    @Override
    public ValueEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return resolve(enumType, rs.getInt(columnName)).orElse(null);
    }

    @Override
    public ValueEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return resolve(enumType, rs.getInt(columnIndex)).orElse(null);
    }

    @Override
    public ValueEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return resolve(enumType, cs.getInt(columnIndex)).orElse(null);
    }


    private Optional<E> resolve(Class<E> enumClass, Integer value) {
        if (enumClass == null || value == null) {
            return Optional.empty();
        }

        E[] enumConstants = enumClass.getEnumConstants();

        for (E e : enumConstants) {
            if (e.value() == value) {
                return Optional.of(e);
            }
        }

        return Optional.empty();
    }
}
