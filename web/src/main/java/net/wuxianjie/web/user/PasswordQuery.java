package net.wuxianjie.web.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class PasswordQuery {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 用户名。
   */
  private String username;

  /**
   * 旧密码。
   */
  @NotBlank(message = "旧密码不能为空")
  @Length(message = "旧密码长度需在 3 到 64 个字符之间", min = 3, max = 64)
  private String oldPassword;

  /**
   * 新密码。
   */
  @NotBlank(message = "新密码不能为空")
  @Length(message = "新密码长度需在 3 到 64 个字符之间", min = 3, max = 64)
  private String newPassword;
}
