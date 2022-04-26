package net.wuxianjie.springbootcore.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Optional;

/**
 * 可用的 Spring Security 授权角色。
 *
 * @author 吴仙杰
 */
@Getter
@ToString
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum Role {

  USER("user"),

  ADMIN("admin");

  private static final Role[] VALUES;

  static {
    VALUES = values();
  }

  @JsonValue
  private final String value;

  /**
   * 将字符串值解析为枚举常量。
   *
   * @param value 字符串值
   * @return 字符串值所对应的枚举常量
   */
  public static Optional<Role> resolve(String value) {
    return Optional.ofNullable(value)
      .flatMap(val -> Arrays.stream(VALUES)
        .filter(role -> StrUtil.equals(val, role.value))
        .findFirst());
  }
}
