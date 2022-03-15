package net.wuxianjie.web.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.springbootcore.handler.YesOrNo;
import net.wuxianjie.springbootcore.validator.EnumValidator;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserQuery {

  /**
   * 用户名。
   */
  @Pattern(message = "用户名只能包含汉字、字母、数字和下划线，且最多包含 64 个字符",
      regexp = "(^$|^[\\u4E00-\\u9FA5A-Za-z0-9_]{0,64}$)")
  private String username;

  /**
   * 启用状态：1：启用，0：禁用。
   */
  @EnumValidator(message = "启用状态错误", value = YesOrNo.class)
  private Integer enabled;
}
