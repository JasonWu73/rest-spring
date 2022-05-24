package net.wuxianjie.web.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.web.security.SysRole;

import java.time.LocalDateTime;

/**
 * 用户列表项数据传输对象。
 *
 * @author 吴仙杰
 */
@Data
@JsonInclude
public class UserItemDto {

  /**
   * 用户 id。
   */
  private Integer userId;

  /**
   * 修改时间，格式为 yyyy-MM-dd HH:mm:ss。
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
   * 用户绑定的角色，多个角色以英文逗号分隔。
   *
   * @see SysRole#value()
   */
  private String roles;
}
