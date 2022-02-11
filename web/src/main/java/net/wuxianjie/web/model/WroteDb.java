package net.wuxianjie.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库写操作（新增、删除、修改）后的结果
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WroteDb {

  /** 数据库影响的行数 */
  private Integer affectedNum;

  /** 针对本次操作的结果说明 */
  private String message;
}
