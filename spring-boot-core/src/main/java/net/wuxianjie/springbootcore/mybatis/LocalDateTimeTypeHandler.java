package net.wuxianjie.springbootcore.mybatis;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * MyBatis 类型处理器：映射 Java {@link LocalDateTime} 与数据库 DATETIME 数据类型，以及 {@link LocalDate} 与数据库 DATE 数据类型。
 * <p>
 * 全局配置（application.yml）<br>
 * {@code mybatis.type-handlers-package: net.wuxianjie.springbootcore.mybatis}
 * </p>
 *
 * @author 吴仙杰
 */
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    @Override
    public void setNonNullParameter(final PreparedStatement ps,
                                    final int i,
                                    final LocalDateTime param,
                                    final JdbcType jdbcType) throws SQLException {
        ps.setString(i, LocalDateTimeUtil.formatNormal(param));
    }

    @Override
    public LocalDateTime getNullableResult(final ResultSet rs,
                                           final String columnName) throws SQLException {
        return toNullableLocalDateTime(rs.getTimestamp(columnName));
    }

    @Override
    public LocalDateTime getNullableResult(final ResultSet rs,
                                           final int columnIndex) throws SQLException {
        return toNullableLocalDateTime(rs.getTimestamp(columnIndex));
    }

    @Override
    public LocalDateTime getNullableResult(final CallableStatement cs,
                                           final int columnIndex) throws SQLException {
        return toNullableLocalDateTime(cs.getTimestamp(columnIndex));
    }

    private static LocalDateTime toNullableLocalDateTime(final Timestamp timestamp) {
        if (timestamp == null) return null;

        return timestamp.toLocalDateTime();
    }
}
