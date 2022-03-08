package net.wuxianjie.core.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据库写操作（新增、删除、修改）的执行结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Written2Db {

    /**
     * 数据库中受影响的行数
     */
    private Integer affectedNum;

    /**
     * 针对本次操作的结果说明
     */
    private String message;
}
