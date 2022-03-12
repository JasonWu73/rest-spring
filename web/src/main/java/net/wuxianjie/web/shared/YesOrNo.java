package net.wuxianjie.web.shared;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public enum YesOrNo implements ValueEnum {

  NO(0),

  YES(1);

  @JsonValue
  private final int value;

  @Override
  public int value() {
    return value;
  }
}
