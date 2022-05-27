package net.wuxianjie.springbootcore.paging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页查询结果。
 *
 * @param <E> 列表项类型
 * @author 吴仙杰
 * @see ResultOfPaging
 * @see PagingOffsetFieldPaddingAspect
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultOfPaging<E> {

  /**
   * 页码。
   */
  private int pageNo;

  /**
   * 每页条数。
   */
  private int pageSize;

  /**
   * 总数。
   */
  private long total;

  /**
   * 具体数据列表。
   */
  private List<E> list;

  public ResultOfPaging(RequestOfPaging paging, long total, List<E> list) {
    this.pageNo = paging.getPageNo();
    this.pageSize = paging.getPageSize();
    this.total = total;
    this.list = list;
  }
}
