package net.wuxianjie.springbootcore.mybatis;

import net.wuxianjie.springbootcore.shared.CommonValues;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MyBatis 类型处理器：映射 Java {@link LocalDateTime} 与数据库日期时间数据类型。
 * <p>
 * 全局配置（application.yml）：<br>
 * {@code mybatis.type-handlers-package: net.wuxianjie.springbootcore.mybatis}
 * </p>
 *
 * @author 吴仙杰
 */
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            LocalDateTime parameter,
            JdbcType jdbcType
    ) throws SQLException {
        ps.setString(i, parameter.format(
                DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT)));
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        return toNullableLocalDateTime(rs.getTimestamp(columnName));
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        return toNullableLocalDateTime(rs.getTimestamp(columnIndex));
    }

    @Override
    public LocalDateTime getNullableResult(
            CallableStatement cs,
            int columnIndex
    ) throws SQLException {
        return toNullableLocalDateTime(cs.getTimestamp(columnIndex));
    }

    private static LocalDateTime toNullableLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }
}
