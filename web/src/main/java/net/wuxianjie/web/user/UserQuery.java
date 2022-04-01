package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.validator.EnumValidator;
import net.wuxianjie.springbootcore.validator.group.GroupOne;
import net.wuxianjie.springbootcore.validator.group.GroupTwo;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 用户查询参数。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserQuery {

    /**
     * 用户 ID。
     */
    private Integer userId;

    /**
     * 用户名。
     */
    @NotBlank(message = "用户名不能为空", groups = GroupOne.class)
    @Pattern(message = "用户名只能包含汉字、字母、数字和下划线，且最多包含 32 个字符",
            regexp = "(^$|^[\\u4E00-\\u9FA5A-Za-z0-9_]{0,32}$)")
    private String username;

    /**
     * 明文密码，无需核验数据库中的原密码，直接使用该密码重置。
     */
    @NotBlank(message = "密码不能为空", groups = GroupOne.class)
    @Length(message = "密码长度需在 3 到 64 个字符之间", min = 3, max = 64)
    private String password;

    /**
     * 启用状态：1：启用，0：禁用。
     */
    @NotNull(message = "启用状态不能为空", groups = GroupOne.class)
    @EnumValidator(message = "启用状态不合法", value = YesOrNo.class)
    private Integer enabled;

    /**
     * 用户绑定的角色，多个角色以英文逗号分隔。
     *
     * @see Role#value()
     */
    @Length(message = "角色长度不能超过 100 个字符", max = 100)
    @Pattern(message = "角色只能包含 user 或 admin，且多个角色需以英文逗号分隔",
            regexp = "(^$|^(admin|user|,)*$)")
    private String roles;

    /**
     * 旧密码，需要与数据库中的哈希匹配。
     */
    @NotBlank(message = "旧密码不能为空", groups = GroupTwo.class)
    @Length(message = "旧密码长度需在 3 到 64 个字符之间", min = 3, max = 64)
    private String oldPassword;

    /**
     * 新密码，旧密码验证通过后才可设置。
     */
    @NotBlank(message = "新密码不能为空", groups = GroupTwo.class)
    @Length(message = "新密码长度需在 3 到 64 个字符之间", min = 3, max = 64)
    private String newPassword;
}
