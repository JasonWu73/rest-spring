package net.wuxianjie.springbootcore.mybatis;

import cn.hutool.core.date.LocalDateTimeUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * MyBatis 类型处理器，映射 Java {@link LocalDate} 与数据库日期时间数据类型。
 *
 * <p>
 * 全局配置（application.yml）<br>
 * {@code mybatis.type-handlers-package: net.wuxianjie.springbootcore.mybatis}
 * </p>
 *
 * @author 吴仙杰
 */
public class LocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

  @Override
  public void setNonNullParameter(PreparedStatement ps,
                                  int i,
                                  LocalDate param,
                                  JdbcType jdbcType) throws SQLException {
    ps.setString(i, LocalDateTimeUtil.formatNormal(param));
  }

  @Override
  public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return toNullableLocalDate(rs.getString(columnName));
  }

  @Override
  public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return toNullableLocalDate(rs.getString(columnIndex));
  }

  @Override
  public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return toNullableLocalDate(cs.getString(columnIndex));
  }

  private static LocalDate toNullableLocalDate(String dateStr) {
    if (dateStr == null) return null;

    return LocalDateTimeUtil.parseDate(dateStr, DateTimeFormatter.ISO_DATE);
  }
}
