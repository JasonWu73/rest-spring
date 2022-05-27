package net.wuxianjie.web.user;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 重置用户密码请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class RequestOfResetUserPassword {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 密码。
   */
  @NotBlank(message = "密码不能为空")
  @Length(message = "密码长度需在 3 到 32 个字符之间", min = 3, max = 32)
  private String password;
}
