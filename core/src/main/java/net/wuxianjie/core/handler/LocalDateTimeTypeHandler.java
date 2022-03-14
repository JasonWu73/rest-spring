package net.wuxianjie.core.handler;

import net.wuxianjie.core.shared.CommonValues;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.lang.Nullable;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MyBatis {@link LocalDateTime} 的类型转换器，
 * 以解决某些 JDBC 不支持 {@link LocalDateTime} 转换的问题，如 SQLite JDBC4。
 */
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

  @Override
  public void setNonNullParameter(PreparedStatement ps,
                                  int i,
                                  LocalDateTime parameter,
                                  JdbcType jdbcType) throws SQLException {
    // MyBatis 默认会将 java.time.LocalDateTime 映射为数据库 Timestamp，
    // 但对于 SQLite 而言，使用 Timestamp 类型会有问题（导致查询不到结果），
    // 故将日期全部转为字符串比较
    final DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT);

    ps.setString(i, parameter.format(formatter));
  }

  @Override
  public LocalDateTime getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    final Timestamp timestamp = rs.getTimestamp(columnName);

    return toLocalDateTime(timestamp);
  }

  @Override
  public LocalDateTime getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    final Timestamp timestamp = rs.getTimestamp(columnIndex);

    return toLocalDateTime(timestamp);
  }

  @Override
  public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    final Timestamp timestamp = cs.getTimestamp(columnIndex);

    return toLocalDateTime(timestamp);
  }

  @Nullable
  private static LocalDateTime toLocalDateTime(Timestamp timestamp) {
    if (timestamp != null) {
      return timestamp.toLocalDateTime();
    }

    return null;
  }
}
