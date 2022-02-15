package net.wuxianjie.web.handler;

import net.wuxianjie.core.constant.CommonValues;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * MyBatis LocalDateTime的类型转换器，这是在SQLite JDBC4不支持{@code LocalDateTime}转换的解决方式
 *
 * @author 吴仙杰
 * @see <a href="https://github.com/mybatis/mybatis-3/issues/1644">LocalDateTimeTypeHandler failing with TIMESTAMPTZ · Issue #1644 · mybatis/mybatis-3</a>
 */
public class LocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType)
      throws SQLException {
    // MyBatis默认会将java.time.LocalDateTime映射为数据库Timestamp
    // 对于SQLite而言，使用Timestamp类型会有问题（导致查询不到结果），故将日期全部转为字符串比较
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonValues.DATE_TIME_FORMAT);
    ps.setString(i, parameter.format(formatter));
  }

  @Override
  public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
    final Timestamp timestamp = rs.getTimestamp(columnName);
    return getLocalDateTime(timestamp);
  }

  @Override
  public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    final Timestamp timestamp = rs.getTimestamp(columnIndex);
    return getLocalDateTime(timestamp);
  }

  @Override
  public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    final Timestamp timestamp = cs.getTimestamp(columnIndex);
    return getLocalDateTime(timestamp);
  }

  private static LocalDateTime getLocalDateTime(Timestamp timestamp) {
    if (timestamp != null) {
      return timestamp.toLocalDateTime();
    }
    return null;
  }
}
