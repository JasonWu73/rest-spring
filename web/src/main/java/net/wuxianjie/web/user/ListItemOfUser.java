package net.wuxianjie.web.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.web.security.RoleOfMenu;

import java.time.LocalDateTime;

/**
 * 用户数据传输对象。
 *
 * @author 吴仙杰
 */
@Data
@JsonInclude
public class ListItemOfUser {

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
   * 用户绑定的菜单编号，多个菜单编号以英文逗号分隔。
   *
   * @see RoleOfMenu#value()
   */
  private String menus;
}
