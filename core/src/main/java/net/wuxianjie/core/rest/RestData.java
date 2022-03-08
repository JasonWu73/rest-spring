package net.wuxianjie.core.rest;

import lombok.Data;

@Data
public class RestData<T> {

    /**
     * 错误码：0=成功，1=失败
     */
    private ErrorCode error;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 数据结果
     */
    private T data;
}
