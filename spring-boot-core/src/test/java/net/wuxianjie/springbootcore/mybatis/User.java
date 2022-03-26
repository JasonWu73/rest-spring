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
    private LocalDateTime createTime;
    private String username;
    private LocalDate birthday;
    private YesOrNo enabled;
}
