package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordQuery {

  /**
   * 用户 ID。
   */
  private Integer userId;

  /**
   * 旧明文密码，需要核验数据库中的原密码。
   */
  @NotBlank(message = "旧密码不能为空")
  @Length(message = "旧密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
  private String oldPassword;

  /**
   * 新明文密码，旧密码核验通过后才可设置。
   */
  @NotBlank(message = "新密码不能为空")
  @Length(message = "新密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
  private String newPassword;
}
