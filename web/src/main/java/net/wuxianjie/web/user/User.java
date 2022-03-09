package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.core.security.Role;
import net.wuxianjie.web.shared.YesOrNo;

import java.time.LocalDateTime;

/**
 * 用户表。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户表 ID。
     */
    private Integer userId;

    /**
     * 记录创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 记录修改时间。
     */
    private LocalDateTime modifyTime;

    /**
     * 启用状态：1：启用，0：禁用。
     */
    private YesOrNo enabled;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 哈希密码。
     */
    private String hashedPassword;

    /**
     * 用户绑定的角色，多个角色以英文逗号分隔。
     *
     * @see Role#value()
     */
    private String roles;
}
