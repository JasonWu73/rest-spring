package net.wuxianjie.web.user;

import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.security.Role;

import java.time.LocalDateTime;

/**
 * 用户表。
 *
 * @author 吴仙杰
 */
@Data
public class User {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 创建时间。
   */
  private LocalDateTime createTime;

  /**
   * 修改时间。
   */
  private LocalDateTime modifyTime;

  /**
   * 启用状态：1：启用，0：禁用。
   */
  private YesOrNo enabled;

  /**
   * 用户名。
   */
  private String username;

  /**
   * 哈希密码。
   */
  private String hashedPassword;

  /**
   * 用户绑定的角色，多个角色以英文逗号分隔。
   *
   * @see Role#value()
   */
  private String roles;
}
