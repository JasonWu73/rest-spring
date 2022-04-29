package net.wuxianjie.web.user;

import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.validator.EnumValidator;
import net.wuxianjie.springbootcore.validator.group.GroupOne;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 新增或修改用户请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class SaveOrUpdateUserQuery {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 启用状态：1：启用，0：禁用。
   */
  @NotNull(message = "启用状态不能为空", groups = GroupOne.class)
  @EnumValidator(message = "启用状态不合法", value = YesOrNo.class)
  private Integer enabled;

  /**
   * 用户名。
   */
  @NotBlank(message = "用户名不能为空", groups = GroupOne.class)
  @Pattern(message = "用户名只能包含汉字、字母、数字和下划线，且最多包含 32 个字符", regexp = "(^$|^[\\u4E00-\\u9FA5A-Za-z0-9_]{0,32}$)")
  private String username;

  /**
   * 密码。
   */
  @NotBlank(message = "密码不能为空", groups = GroupOne.class)
  @Length(message = "密码长度需在 3 到 64 个字符之间", min = 3, max = 64)
  private String password;


  /**
   * 用户绑定的角色，只能包含 admin 或 user，且多个角色以英文逗号分隔。
   *
   * @see Role#value()
   */
  @Pattern(message = "角色只能包含 user 或 admin，且多个角色需以英文逗号分隔", regexp = "(^$|^(admin|user|,)*$)")
  private String roles;
}
