package net.wuxianjie.core.paging;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用于分页查询的参数
 */
@Data
public class PagingQuery {

    /**
     * 页码，从 0 开始
     */
    @NotNull(message = "页码不能为空")
    @Min(message = "页码不能小于 0", value = 0)
    private Integer pageNo;

    /**
     * 每页条数
     */
    @NotNull(message = "每页条数不能为空")
    @Min(message = "每页条数不能小于 1", value = 1)
    private Integer pageSize;

    /**
     * MySQL、SQLite 等数据库的偏移量 OFFSET
     *
     * <p>由 {@link PagingParamPaddingAspect} 自动识别方法并调用 {@link #setOffset()} 设置</p>
     */
    @Setter(AccessLevel.NONE)
    private Integer offset;

    /**
     * MySQL、SQLite 等数据库的偏移量 OFFSET，如：
     *
     * <ul>
     *   <li>SELECT * FROM table_name LIMIT #{pageSize} OFFSET #{offset}</li>
     *   <li>SELECT * FROM table_name LIMIT #{offset}, #{pageSize}</li>
     * </ul>
     */
    public void setOffset() {
        offset = pageNo * pageSize;
    }
}
