package net.wuxianjie.springbootcore.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST API 服务响应结果。
 *
 * @param <T> 数据结果泛型
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestData<T> {

    /**
     * 错误码：0：成功，1：失败。
     */
    private ErrorCode error;

    /**
     * 错误信息。
     */
    private String errMsg;

    /**
     * 数据结果。
     */
    private T data;
}
