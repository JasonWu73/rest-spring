package net.wuxianjie.web.user;

import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.validator.EnumValidator;

/**
 * 查询用户请求参数。
 *
 * @author 吴仙杰
 */
@Data
public class RequestOfGetUser {

  /**
   * 用户名。
   */
  private String username;

  /**
   * 启用状态：1：启用，0：禁用。
   */
  @EnumValidator(message = "启用状态不合法", value = YesOrNo.class)
  private Integer enabled;
}
