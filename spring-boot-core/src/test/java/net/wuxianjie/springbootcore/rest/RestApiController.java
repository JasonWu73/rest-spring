package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import net.wuxianjie.springbootcore.shared.BadRequestException;
import net.wuxianjie.springbootcore.shared.ConflictException;
import net.wuxianjie.springbootcore.shared.InternalException;
import net.wuxianjie.springbootcore.shared.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;

/**
 * @author 吴仙杰
 */
@Validated
@RestController
class RestApiController {

    @GetMapping(value = "/html", produces = MediaType.TEXT_HTML_VALUE)
    String getHtml() {
        return "<h1>Hello World</h1>";
    }

    @PostMapping(value = "/body")
    User getBody(@RequestBody User user) {
        return user;
    }

    @GetMapping("/required")
    String getRequiredParameter(
            @RequestParam("name") String username,
            @RequestParam("userId") Integer userId
    ) {
        return username + ": " + userId;
    }

    @GetMapping("/validated")
    String getValidateParam(
            @NotBlank(message = "用户名不能为空") String username,
            @NotNull(message = "用户 ID 不能为 null") Integer userId
    ) {
        return username + ": " + userId;
    }

    @GetMapping("/valid")
    String getValidParam(@Valid User user) {
        return user.getUsername() + ": " + user.getUserId();
    }

    @GetMapping("/exception")
    String getException(String type) {
        if (StrUtil.equals(type, "not_found")) {
            throw new NotFoundException("未找到 id 为 x 的数据");
        }

        if (StrUtil.equals(type, "bad_request")) {
            throw new BadRequestException("客户端请求错误");
        }

        if (StrUtil.equals(type, "conflict")) {
            throw new ConflictException("已存在相同数据");
        }

        if (StrUtil.equals(type, "internal")) {
            try {
                return errorMath();
            } catch (Exception e) {
                throw new InternalException("服务内部异常", e);
            }
        }

        if (StrUtil.equals(type, "db")) {
            throw new UncategorizedSQLException(
                    "查询语句",
                    "SELECT field FROM table",
                    new SQLException()
            );
        }

        return errorMath();
    }

    @GetMapping("/void")
    void nothing() {
    }

    @GetMapping("/result/{type}")
    String getResult(@PathVariable String type) {
        if (StrUtil.equals(type, "null")) {
            return null;
        }

        return " Hello World\t\n";
    }

    @Data
    static class User {

        @NotNull(message = "用户 ID 不能为 null")
        private Integer userId;

        @NotBlank(message = "用户名不能为空")
        private String username;
    }

    private String errorMath() {
        return (1 / 0) + "";
    }
}
