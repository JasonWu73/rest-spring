package net.wuxianjie.web.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 系统菜单项角色枚举类。
 *
 * @author 吴仙杰
 */
@Getter
@ToString
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum SysRole {

  ROLE_SU("su", "超级账号（非菜单项角色）"),

  ROLE_SYS("sys", "系统管理"),
  ROLE_USER("user", "用户管理"),
  ROLE_USER_ADD("user_add", "新增用户"),
  ROLE_USER_UPDATE("user_update", "修改用户"),
  ROLE_USER_RESET_PWD("user_reset_pwd", "重置用户密码"),
  ROLE_USER_DEL("user_del", "删除用户"),

  ROLE_OP_LOG("op_log", "操作日志");

  private static final SysRole[] VALUES;

  static {
    VALUES = values();
  }

  /**
   * 角色值。
   */
  @JsonValue
  private final String value;

  /**
   * 角色描述。
   */
  private final String message;

  private static final List<MenuItem> MENUS = new ArrayList<>() {{
    add(new MenuItem(1, ROLE_SYS.message, ROLE_SYS.value, 0, new ArrayList<>() {{
      add(new MenuItem(11, ROLE_USER.message, ROLE_USER.value, 1, new ArrayList<>() {{
        add(new MenuItem(111, ROLE_USER_ADD.message, ROLE_USER_ADD.value, 1, null));
        add(new MenuItem(112, ROLE_USER_UPDATE.message, ROLE_USER_UPDATE.value, 1, null));
        add(new MenuItem(113, ROLE_USER_RESET_PWD.message, ROLE_USER_RESET_PWD.value, 1, null));
        add(new MenuItem(114, ROLE_USER_DEL.message, ROLE_USER_DEL.value, 1, null));
      }}));

      add(new MenuItem(12, ROLE_OP_LOG.message, ROLE_OP_LOG.value, 0, null));
    }}));
  }};

  /**
   * 获取符合 Spring Security 的角色层级结构字符串。
   *
   * @return 角色层级结构字符串
   */
  public static String getRoleHierarchyStr() {
    return StrUtil.format(
      "{} > {}\n" +

        "{} > {}\n" +
        "{} > {}\n" +
        "{} > {}\n" +
        "{} > {}\n" +
        "{} > {}\n" +

        "{} > {}",
      ROLE_SU.name(), ROLE_SYS.name(),

      ROLE_SYS.name(), ROLE_USER.name(),
      ROLE_USER.name(), ROLE_USER_ADD.name(),
      ROLE_USER.name(), ROLE_USER_UPDATE.name(),
      ROLE_USER.name(), ROLE_USER_RESET_PWD.name(),
      ROLE_USER.name(), ROLE_USER_DEL.name(),

      ROLE_SYS.name(), ROLE_OP_LOG.name()
    );
  }

  /**
   * 获取全部菜单项。
   *
   * @return 树形结构的全部菜单顶数据
   */
  public static List<MenuItem> getAllMenus() {
    return MENUS;
  }

  /**
   * 将字符串值解析为枚举常量。
   *
   * @param value 字符串值
   * @return {@link SysRole} 的 Optional 包装对象
   */
  public static Optional<SysRole> resolve(String value) {
    return Optional.ofNullable(value)
      .flatMap(val -> Arrays.stream(VALUES)
        .filter(role -> StrUtil.equals(val, role.value))
        .findFirst());
  }
}
