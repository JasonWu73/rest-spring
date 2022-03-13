package net.wuxianjie.web.shared;

import lombok.NoArgsConstructor;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 处理数据库 int 类型与枚举值的映射规则。
 */
@NoArgsConstructor
public class EnumTypeHandler<E extends Enum<?> & ValueEnum>
  extends BaseTypeHandler<ValueEnum> {

  private Class<E> enumType;

  public EnumTypeHandler(Class<E> enumType) {
    if (enumType == null) {
      throw new IllegalArgumentException("enumType 参数不能为 null");
    }

    this.enumType = enumType;
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps,
                                  int i,
                                  ValueEnum parameter,
                                  JdbcType jdbcType
  ) throws SQLException {
    ps.setInt(i, parameter.value());
  }

  @Override
  public ValueEnum getNullableResult(ResultSet rs, String columnName)
    throws SQLException {
    return EnumUtils.resolve(enumType, rs.getInt(columnName));
  }

  @Override
  public ValueEnum getNullableResult(ResultSet rs, int columnIndex)
    throws SQLException {
    return EnumUtils.resolve(enumType, rs.getInt(columnIndex));
  }

  @Override
  public ValueEnum getNullableResult(CallableStatement cs, int columnIndex)
    throws SQLException {
    return EnumUtils.resolve(enumType, cs.getInt(columnIndex));
  }
}
