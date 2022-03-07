package net.wuxianjie.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.core.constant.YesOrNo;
import net.wuxianjie.core.validator.EnumValidator;
import net.wuxianjie.core.validator.group.Save;
import net.wuxianjie.core.validator.group.Update;
import net.wuxianjie.web.model.User;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDto {

    private Integer userId;

    private LocalDateTime modifyTime;

    /**
     * 是否已启用，1=启用，0=禁用
     *
     * <p>详见：{@link net.wuxianjie.core.constant.YesOrNo}</p>
     */
    @NotNull(message = "启用状态不能为空", groups = Save.class)
    @EnumValidator(message = "启用状态错误", value = YesOrNo.class)
    private Integer enabled;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = Save.class)
    @Length(message = "用户名长度需在 2 到 25 个字符之间", min = 2, max = 25)
    @Pattern(message = "用户名只能包含汉字、字母、数字和下划线",
            regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,}$")
    private String username;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空", groups = Save.class)
    @Length(message = "密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
    private String password;

    private String hashedPassword;

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码不能为空", groups = Update.class)
    @Length(message = "密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
    private String oldPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空", groups = Update.class)
    @Length(message = "密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
    private String newPassword;

    /**
     * 分配的角色，以英文逗号分隔，只能包含 user 或 admin
     */
    @Pattern(message = "角色只能包含 user 或 admin，且必须以英文逗号分隔",
            regexp = "^(admin|user)(admin|user|,)*$")
    private String roles;

    public UserDto(final User user) {
        this.userId = user.getUserId();
        this.enabled = user.getEnabled().value();
        this.username = user.getUsername();
        this.hashedPassword = user.getHashedPassword();
        this.roles = user.getRoles();
    }
}
