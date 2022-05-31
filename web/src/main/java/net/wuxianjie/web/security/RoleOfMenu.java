package net.wuxianjie.web.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * 系统菜单项角色枚举类。
 *
 * @author 吴仙杰
 */
@Getter
@Accessors(fluent = true)
@ToString
@RequiredArgsConstructor
public enum RoleOfMenu {

  ROLE_ROOT("root", "全部"),

  ROLE_SYS("sys", "系统管理"),

  ROLE_USER("user", "用户管理"),
  ROLE_USER_ADD("user_add", "新增用户"),
  ROLE_USER_UPDATE("user_update", "修改用户"),
  ROLE_USER_RESET_PWD("user_reset_pwd", "重置密码"),
  ROLE_USER_DEL("user_del", "删除用户"),

  ROLE_ROLE("role", "角色管理"),
  ROLE_ROLE_ADD("role_add", "新增角色"),
  ROLE_ROLE_UPDATE("role_update", "修改角色"),
  ROLE_ROLE_DEL("role_del", "删除角色"),

  ROLE_LOGIN_LOG("login_log", "登录日志"),

  ROLE_OP_LOG("op_log", "操作日志");

  private static final RoleOfMenu[] VALUES;

  static {
    VALUES = values();
  }

  /**
   * 菜单编号。
   */
  @JsonValue
  private final String value;

  /**
   * 菜单名称。
   */
  private final String msg;

  private static final MenuItem MENU = new MenuItem(ROLE_ROOT.msg, ROLE_ROOT.value, new ArrayList<>() {{
    add(new MenuItem(ROLE_SYS.msg, ROLE_SYS.value, new ArrayList<>() {{
      add(new MenuItem(ROLE_USER.msg, ROLE_USER.value, new ArrayList<>() {{
        add(new MenuItem(ROLE_USER_ADD.msg, ROLE_USER_ADD.value, null));
        add(new MenuItem(ROLE_USER_UPDATE.msg, ROLE_USER_UPDATE.value, null));
        add(new MenuItem(ROLE_USER_RESET_PWD.msg, ROLE_USER_RESET_PWD.value, null));
        add(new MenuItem(ROLE_USER_DEL.msg, ROLE_USER_DEL.value, null));
      }}));

      add(new MenuItem(ROLE_ROLE.msg, ROLE_ROLE.value, new ArrayList<>() {{
        add(new MenuItem(ROLE_ROLE_ADD.msg, ROLE_ROLE_ADD.value, null));
        add(new MenuItem(ROLE_ROLE_UPDATE.msg, ROLE_ROLE_UPDATE.value, null));
        add(new MenuItem(ROLE_ROLE_DEL.msg, ROLE_ROLE_DEL.value, null));
      }}));

      add(new MenuItem(ROLE_LOGIN_LOG.msg, ROLE_LOGIN_LOG.value, null));

      add(new MenuItem(ROLE_OP_LOG.msg, ROLE_OP_LOG.value, null));
    }}));
  }});

  /**
   * 获取符合 Spring Security 的角色层级结构字符串。
   *
   * @return 角色层级结构字符串
   */
  public static String getRoleHierarchyStr() {
    String template = "{} > {}\n" +

      "{} > {}\n" +
      "{} > {}\n" +
      "{} > {}\n" +
      "{} > {}\n" +
      "{} > {}\n" +

      "{} > {}\n" +
      "{} > {}\n" +
      "{} > {}\n" +
      "{} > {}\n" +

      "{} > {}\n" +

      "{} > {}";

    return StrUtil.format(
      template,
      ROLE_ROOT.name(), ROLE_SYS.name(),

      ROLE_SYS.name(), ROLE_USER.name(),
      ROLE_USER.name(), ROLE_USER_ADD.name(),
      ROLE_USER.name(), ROLE_USER_UPDATE.name(),
      ROLE_USER.name(), ROLE_USER_RESET_PWD.name(),
      ROLE_USER.name(), ROLE_USER_DEL.name(),

      ROLE_SYS.name(), ROLE_ROLE.name(),
      ROLE_ROLE.name(), ROLE_ROLE_ADD.name(),
      ROLE_ROLE.name(), ROLE_ROLE_UPDATE.name(),
      ROLE_ROLE.name(), ROLE_ROLE_DEL.name(),

      ROLE_SYS.name(), ROLE_LOGIN_LOG.name(),

      ROLE_SYS.name(), ROLE_OP_LOG.name()
    );
  }

  /**
   * 获取全部菜单项。
   *
   * @return 树形结构的全部菜单项数据
   */
  public static MenuItem getAllMenus() {
    return MENU;
  }

  /**
   * 将字符串值解析为枚举常量。
   *
   * @param value 字符串值
   * @return {@link RoleOfMenu} 的 {@link Optional} 包装对象
   */
  public static Optional<RoleOfMenu> resolve(String value) {
    return Optional.ofNullable(value)
      .flatMap(val -> Arrays.stream(VALUES)
        .filter(role -> StrUtil.equals(val, role.value))
        .findFirst());
  }
}
