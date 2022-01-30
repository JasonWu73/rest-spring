package net.wuxianjie.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于分页数据展示的数据类。
 *
 * @param <T> 具体数据列表的类类型
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingData<T> {

  /**
   * 总条数。
   */
  private long total;

  /**
   * 当前页码。
   */
  private int current;

  /**
   * 每页条数。
   */
  private int size;

  /**
   * 具体数据列表。
   */
  private T list;
}
