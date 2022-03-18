package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 修改用户密码的请求参数。
 *
 * @author 吴仙杰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordQuery {

    /**
     * 用户 ID。
     */
    private Integer userId;

    /**
     * 旧密码，需要与数据库中的哈希匹配。
     */
    @NotBlank(message = "旧密码不能为空")
    @Length(message = "旧密码长度需在 3 到 100 个字符之间", min = 3, max = 100)
    private String oldPassword;

    /**
     * 新密码，旧密码验证通过后才可设置。
     */
    @NotBlank(message = "新密码不能为空")
    @Length(message = "新密码长度需在 3 到 100 个字符之间", min = 3, max = 100)
    private String newPassword;
}
