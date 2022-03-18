package net.wuxianjie.springbootcore.handler;

import net.wuxianjie.springbootcore.shared.CommonValues;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * MyBatis {@link LocalDateTime} 的类型转换器，
 * 以解决某些 JDBC 不支持 {@link LocalDateTime} 转换的问题，如 SQLite JDBC4。
 *
 * <p>全局配置（application.yml）：</p>
 *
 * <p>{@code mybatis.type-handlers-package: net.wuxianjie.springbootcore.handler}</p>
 *
 * @author 吴仙杰
 */
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        // MyBatis 默认会将 java.time.LocalDateTime 映射为数据库 Timestamp，
        // 但对于 SQLite 而言，这会导致查询不到数据，故将日期全部转为字符串比较
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT);
        ps.setString(i, parameter.format(formatter));
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toLocalDateTime(rs.getTimestamp(columnName)).orElse(null);
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toLocalDateTime(rs.getTimestamp(columnIndex)).orElse(null);
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toLocalDateTime(cs.getTimestamp(columnIndex)).orElse(null);
    }

    private static Optional<LocalDateTime> toLocalDateTime(Timestamp timestamp) {
        if (timestamp != null) {
            return Optional.of(timestamp.toLocalDateTime());
        }

        return Optional.empty();
    }
}
