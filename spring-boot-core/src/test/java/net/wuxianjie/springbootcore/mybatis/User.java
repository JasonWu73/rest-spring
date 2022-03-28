package net.wuxianjie.springbootcore.mybatis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author 吴仙杰
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
class User {

    private Integer userId;
    private String username;
    private YesOrNo enabled;
    private LocalDateTime createTime;
    private LocalDate birthday;
}
