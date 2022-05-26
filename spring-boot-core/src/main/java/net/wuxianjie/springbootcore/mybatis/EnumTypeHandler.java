package net.wuxianjie.springbootcore.mybatis;

import lombok.NoArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

/**
 * MyBatis 类型处理器，映射 Java 枚举常量与数据库 INTEGER 数据类型。
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

  public EnumTypeHandler(Class<E> enumType) {
    if (enumType == null) throw new IllegalArgumentException("enumType 不能为 null");

    this.enumType = enumType;
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps,
                                  int i,
                                  ValueEnum parameter,
                                  JdbcType jdbcType) throws SQLException {
    ps.setInt(i, parameter.value());
  }

  @Override
  public ValueEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return toNullableEnum(enumType, rs.getInt(columnName));
  }

  @Override
  public ValueEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return toNullableEnum(enumType, rs.getInt(columnIndex));
  }

  @Override
  public ValueEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return toNullableEnum(enumType, cs.getInt(columnIndex));
  }

  private E toNullableEnum(Class<E> enumClass, int val) {
    if (enumClass == null) throw new IllegalArgumentException("enumClass 不能为 null");

    E[] enumConstants = Optional.ofNullable(enumClass.getEnumConstants())
      .orElseThrow(() -> new IllegalArgumentException("enumClass 不是枚举类型"));

    return Arrays.stream(enumConstants)
      .filter(e -> val == e.value())
      .findFirst()
      .orElse(null);
  }
}
