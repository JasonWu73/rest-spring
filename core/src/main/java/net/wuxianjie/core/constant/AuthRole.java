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
  USER("USER"),

  /** 管理员 */
  ADMIN("ADMIN");

  @JsonValue
  private final String value;
}
