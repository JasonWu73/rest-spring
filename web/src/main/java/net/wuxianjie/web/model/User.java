package net.wuxianjie.web.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
public class User {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 修改时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 用户名
     */
    private String username;

    /**
     * 哈希密码
     */
    private String hashedPassword;

    /**
     * 账号角色，以英文逗号（{@code ,}）分隔，
     * 全部为小写字母，且不包含 {@code ROLE_} 前缀
     */
    private String roles;
}
