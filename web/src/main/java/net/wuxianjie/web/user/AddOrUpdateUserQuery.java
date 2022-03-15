package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.handler.YesOrNo;
import net.wuxianjie.springbootcore.validator.EnumValidator;
import net.wuxianjie.springbootcore.validator.group.Add;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateUserQuery {

  /**
   * 用户 ID。
   */
  private Integer userId;

  /**
   * 用户名。
   */
  @NotBlank(message = "用户名不能为空", groups = Add.class)
  @Pattern(message = "用户名只能包含汉字、字母、数字和下划线，且最多包含 64 个字符",
      regexp = "(^$|^[\\u4E00-\\u9FA5A-Za-z0-9_]{0,64}$)")
  private String username;

  /**
   * 明文密码，无需核验数据库中的原密码，直接使用该密码重置。
   */
  @NotBlank(message = "密码不能为空", groups = Add.class)
  @Length(message = "密码长度需在 3 到 25 个字符之间", min = 3, max = 25)
  private String password;

  /**
   * 是否已启用：1=启用，0=禁用。
   */
  @NotNull(message = "启用状态不能为空", groups = Add.class)
  @EnumValidator(message = "启用状态错误", value = YesOrNo.class)
  private Integer enabled;

  /**
   * 用户绑定的角色，多个角色以英文逗号分隔。
   */
  @Pattern(message = "角色只能包含 user 或 admin，且多个角色需以英文逗号分隔",
      regexp = "(^$|^(admin|user|,)*$)")
  private String roles;
}
