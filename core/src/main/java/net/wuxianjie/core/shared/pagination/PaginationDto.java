package net.wuxianjie.core.shared.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于分页数据展示的数据
 *
 * @param <T> 具体数据列表的类类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationDto<T> {

    /**
     * 总数
     */
    private long total;

    /**
     * 当前页码
     */
    private int pageNo;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 具体数据列表
     */
    private T list;
}
