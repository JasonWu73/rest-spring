package net.wuxianjie.springbootcore.paging;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 分页查询参数。
 *
 * @author 吴仙杰
 * @see PagingResult
 * @see PagingOffsetFieldPaddingAspect
 */
@Data
public class PagingQuery {

    /**
     * 页码，从 1 开始。
     */
    @NotNull(message = "页码不能为 null")
    @Min(message = "页码不能小于 1", value = 1)
    private Integer pageNo;

    /**
     * 每页条数。
     */
    @NotNull(message = "每页条数不能为 null")
    @Min(message = "每页条数不能小于 1", value = 1)
    private Integer pageSize;

    /**
     * MySQL、SQLite 等数据库的偏移量 OFFSET。
     *
     * @see PagingOffsetFieldPaddingAspect
     */
    @Setter(AccessLevel.NONE)
    private Integer offset;

    /**
     * MySQL、SQLite 等数据库的偏移量 OFFSET，例如：
     *
     * <ul>
     *   <li>{@code SELECT * FROM table_name LIMIT #{pageSize} OFFSET #{offset}}</li>
     *   <li>{@code SELECT * FROM table_name LIMIT #{offset}, #{pageSize}}</li>
     * </ul>
     */
    public void setOffset() {
        offset = (pageNo - 1) * pageSize;
    }
}
