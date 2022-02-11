package net.wuxianjie.core.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 项目中用于权限管理的角色枚举类
 *
 * @author 吴仙杰
 */
@Accessors(fluent = true)
@Getter
@ToString
@RequiredArgsConstructor
public enum AuthRole {

  /** 普通用户 */
  USER("user"),

  /** 管理员 */
  ADMIN("admin");

  private static final AuthRole[] VALUES;

  static {
    VALUES = values();
  }

  @JsonValue
  private final String value;

  /**
   * 如果可能的话，将给定的角色代码解析为一个{@code AuthRole}
   *
   * @param role 角色代码，区分大小写
   * @return 相应的AuthRole，如果没有找到，则为null
   */
  public static AuthRole resolve(final String role) {
    // 使用缓存的VALUES而不是values()来防止数组分配
    for (AuthRole authRole : VALUES) {
      if (authRole.value.equals(role)) {
        return authRole;
      }
    }
    return null;
  }
}
