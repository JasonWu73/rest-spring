package net.wuxianjie.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.wuxianjie.web.model.User;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDto {

    private Integer userId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private String username;

    private String hashedPassword;

    private String roles;

    public UserDto(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.hashedPassword = user.getHashedPassword();
        this.roles = user.getRoles();
    }
}
