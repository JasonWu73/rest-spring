package net.wuxianjie.web.model;

import lombok.Data;
import net.wuxianjie.core.constant.YesOrNo;

import java.time.LocalDateTime;

/**
 * 用户表
 */
@Data
public class User {

    /**
     * 用户 ID
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 是否已启用，1=启用，0=禁用
     */
    private YesOrNo enabled;

    /**
     * 用户名
     */
    private String username;

    /**
     * 哈希密码
     */
    private String hashedPassword;

    /**
     * 分配的角色，以英文逗号（{@code ,}）分隔，
     * 全部为小写字母，且不包含 {@code ROLE_} 前缀
     */
    private String roles;
}
