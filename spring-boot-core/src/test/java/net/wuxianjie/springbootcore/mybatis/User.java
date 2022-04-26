package net.wuxianjie.springbootcore.mybatis;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 吴仙杰
 */
@Data
class User {

  private Integer userId;
  private String username;
  private YesOrNo enabled;
  private LocalDateTime createTime;
  private LocalDate birthday;
}
