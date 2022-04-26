package net.wuxianjie.springbootcore.rest;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import net.wuxianjie.springbootcore.exception.InternalException;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * @author 吴仙杰
 */
@Validated
@RestController
class ApiTestController {

  @GetMapping(value = "/html", produces = MediaType.TEXT_HTML_VALUE)
  String html() {
    return "<h1>Hello World</h1>";
  }

  @PostMapping("/upload")
  void upload(@RequestParam MultipartFile file) {
  }

  @PostMapping(value = "/body")
  void body(@RequestBody User user) {
  }

  @GetMapping("/required")
  void requiredParam(@RequestParam("userId") Integer userId,
                     @RequestParam("name") String username) {
  }

  @GetMapping("/validated")
  void validateParam(@NotNull(message = "用户 id 不能为 null") Integer userId,
                     @NotBlank(message = "用户名不能为空") String username) {
  }

  @GetMapping("/valid")
  void validParam(@Valid User user) {
  }

  @GetMapping("/exception")
  void customException(String type) {
    if (StrUtil.equals(type, "db")) {
      throw new UncategorizedSQLException(
        "查询语句",
        "SELECT field FROM table",
        new SQLException()
      );
    }

    if (StrUtil.equals(type, "not_found")) {
      throw new NotFoundException("未找到 id 为 x 的数据");
    }

    if (StrUtil.equals(type, "internal")) {
      try {
        doWrongMath();
      } catch (Exception e) {
        throw new InternalException("服务内部异常", e);
      }
    }

    doWrongMath();
  }

  @GetMapping("/void")
  void nothing() {
  }

  @GetMapping("/null")
  String getStringNullResult() {
    return null;
  }

  @GetMapping("/str")
  String getStringResult() {
    return " Hello World\t\n";
  }

  @GetMapping("/bytes")
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

  void doWrongMath() {
    System.out.println(1 / 0);
  }
}
