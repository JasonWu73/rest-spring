package net.wuxianjie.springbootcore.paging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页数据结果。
 *
 * @param <T> 数据列表泛型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingData<T> {

  /**
   * 总数。
   */
  private long total;

  /**
   * 当前页码。
   */
  private int pageNo;

  /**
   * 每页条数。
   */
  private int pageSize;

  /**
   * 数据列表。
   */
  private T list;

  public PagingData(PagingQuery paging, long total, T list) {
    this.pageNo = paging.getPageNo();
    this.pageSize = paging.getPageSize();
    this.total = total;
    this.list = list;
  }
}
