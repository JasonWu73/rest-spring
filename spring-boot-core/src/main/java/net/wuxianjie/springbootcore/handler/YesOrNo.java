package net.wuxianjie.springbootcore.handler;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public enum YesOrNo implements ValueEnum {

  NO(0),

  YES(1);

  private static final YesOrNo[] VALUES;

  static {
    VALUES = values();
  }

  @JsonValue
  private final int value;

  @Override
  public int value() {
    return value;
  }
}
