package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import net.wuxianjie.springbootcore.shared.exception.*;
import org.springframework.http.MediaType;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * @author 吴仙杰
 */
@RestController
@Validated
class ApiTestController {

    @GetMapping(value = "/html", produces = MediaType.TEXT_HTML_VALUE)
    String getHtml() {
        return "<h1>Hello World</h1>";
    }

    @PostMapping(value = "/body")
    User getBody(@RequestBody final User user) {
        return user;
    }

    @GetMapping("/required")
    String getRequiredParameter(@RequestParam("name") final String username,
                                @RequestParam("userId") final Integer userId) {
        return username + ": " + userId;
    }

    @GetMapping("/validated")
    String getValidateParam(@NotBlank(message = "用户名不能为空") final String username,
                            @NotNull(message = "用户 id 不能为 null") final Integer userId) {
        return username + ": " + userId;
    }

    @GetMapping("/valid")
    String getValidParam(@Valid final User user) {
        return user.getUsername() + ": " + user.getUserId();
    }

    @GetMapping("/exception")
    String getException(final String type) {
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

        if (StrUtil.equals(type, "external")) {
            try {
                throw new RuntimeException("请求 Google API 失败");
            } catch (Exception e) {
                throw new ExternalException("外部 API 不可用", e);
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

    @GetMapping("/null-when-object-return-type")
    User getObjectNullResult() {
        return null;
    }

    @GetMapping("/null-when-string-return-type")
    String getStringNullResult() {
        return null;
    }

    @GetMapping("/str")
    String getStringResult() {
        return " Hello World\t\n";
    }

    @GetMapping(value = "/bytes")
    byte[] getBytes() {
        return "Hello Bytes".getBytes(StandardCharsets.UTF_8);
    }

    @Data
    static class User {

        @NotNull(message = "用户 id 不能为 null")
        private Integer userId;

        @NotBlank(message = "用户名不能为空")
        private String username;
    }

    @SuppressWarnings({"divzero", "NumericOverflow"})
    private String errorMath() {
        return (1 / 0) + "";
    }
}
