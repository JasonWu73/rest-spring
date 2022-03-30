package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.Role;

import java.time.LocalDateTime;

/**
 * 用于用户管理的数据传输对象。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserManagerDto {

    /**
     * 用户 ID。
     */
    private Integer userId;

    /**
     * 修改时间，格式为 yyyy-MM-dd HH:mm:ss。
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
     * 用户绑定的角色，多个角色以英文逗号分隔。
     *
     * @see Role#value()
     */
    private String roles;
}
