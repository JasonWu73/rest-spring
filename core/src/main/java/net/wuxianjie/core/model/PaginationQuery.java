package net.wuxianjie.core.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用于分页查询的参数
 *
 * @author 吴仙杰
 */
@Data
public class PaginationQuery {

  /** 页码，从0开始，必填 */
  @NotNull(message = "页码不能为空")
  @Min(value = 0, message = "页码不能小于0")
  private Integer pageNo;

  /** 每页总数，必填 */
  @NotNull(message = "每页总条数不能为空")
  @Min(value = 0, message = "每页总数不能小于1")
  private Integer pageSize;

  /** MySQL、SQLite等数据的OFFSET偏移量，需要<b>手动调用{@link #setOffset()}</b>设置 */
  @Setter(AccessLevel.NONE)
  private Integer offset;

  /**
   * MySQL、SQLite等数据的OFFSET偏移量
   *
   * <p>如偏移量M：</p>
   * <ul>
   *   <li>SELECT * FROM table_name LIMIT N OFFSET M</li>
   *   <li>SELECT * FROM table_name LIMIT M,N</li>
   * </ul>
   */
  public void setOffset() {
     offset = pageNo * pageSize;
  }
}
