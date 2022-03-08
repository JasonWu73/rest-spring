package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.core.validator.EnumValidator;
import net.wuxianjie.core.validator.group.Add;
import net.wuxianjie.core.validator.group.Update;
import net.wuxianjie.web.shared.YesOrNo;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagementOfUser {

    /**
     * 用户表 ID
     */
    private Integer userId;

    /**
     * 记录创建时间
     */
    private LocalDateTime modifyTime;

    /**
     * 是否已启用：1=启用，0=禁用
     */
    @NotNull(message = "启用状态不能为空", groups = Add.class)
    @EnumValidator(message = "启用状态错误", value = YesOrNo.class)
    private Integer enabled;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = Add.class)
    @Length(message = "用户名长度需在 2 到 25 个字符之间", min = 2, max = 25)
    @Pattern(message = "用户名只能包含汉字、字母、数字和下划线", regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,}$")
    private String username;

    /**
     * 哈希密码
     */
    private String hashedPassword;

    /**
     * 用户绑定的角色，多个角色以英文逗号分隔
     */
    @Pattern(message = "角色只能包含 user 或 admin，且多个角色需以英文逗号分隔", regexp = "^(admin|user)(admin|user|,)*$")
    private String roles;

    /**
     * 明文密码，无需核验数据库中的原密码，直接使用该密码重置
     */
    @NotBlank(message = "密码不能为空", groups = Add.class)
    @Length(message = "密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
    private String password;

    /**
     * 旧明文密码，需要核验数据库中的原密码
     */
    @NotBlank(message = "旧密码不能为空", groups = Update.class)
    @Length(message = "旧密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
    private String oldPassword;

    /**
     * 新明文密码，旧密码核验通过后才可设置
     */
    @NotBlank(message = "新密码不能为空", groups = Update.class)
    @Length(message = "新密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
    private String newPassword;

    public ManagementOfUser(User user) {
        this.userId = user.getUserId();
        this.modifyTime = user.getModifyTime();
        this.enabled = user.getEnabled() == null ? null : user.getEnabled().value();
        this.username = user.getUsername();
        this.hashedPassword = user.getHashedPassword();
        this.roles = user.getRoles();
    }
}
