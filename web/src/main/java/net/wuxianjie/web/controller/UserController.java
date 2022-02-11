package net.wuxianjie.web.controller;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.constant.AuthRole;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.model.WroteDb;
import net.wuxianjie.web.service.UserService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;

/**
 * 系统可登录用户的控制器
 *
 * @author 吴仙杰
 */
@Admin
@Validated
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

  private final UserService userService;

  /**
   * 获取用户分页数据
   *
   * @param pageNo 页码，从0开始，必填
   * @param pageSize 每页显示条数，必填
   * @param username 用户名
   * @return 用户分页数据
   */
  @GetMapping("/users")
  public PaginationData<List<User>> loadUsers(
      @RequestParam Integer pageNo,
      @RequestParam Integer pageSize,
      @RequestParam(required = false) final String username) {
    return userService.loadUsers(pageNo, pageSize, StringUtils.generateDbFuzzyStr(username));
  }

  /**
   * 新增用户
   *
   * @param userToAdd 需要入库的用户数据
   * @return 新增结果
   */
  @PostMapping("/user")
  public WroteDb saveUser(@Valid @RequestBody final UserToAdd userToAdd) {
    final String roles = toDistinctAndDeduplicateAndLowerCaseRoles(userToAdd.getRoles());
    validateRoles(roles);
    userToAdd.setRoles(roles);
    return userService.saveUser(userToAdd);
  }

  private void validateRoles(final String roles) {
    if (StrUtil.isEmpty(roles)) {
      return;
    }

    final boolean hasInvalidRole = Arrays.stream(roles.split(","))
        .anyMatch(x -> AuthRole.resolve(x) == null);

    if (hasInvalidRole) {
      throw new BadRequestException("包含无效的角色");
    }
  }

  private String toDistinctAndDeduplicateAndLowerCaseRoles(final String roles) {
    if (StrUtil.isEmpty(roles)) {
      return null;
    }

    return Arrays.stream(roles.split(","))
        .reduce("", (s, s2) -> {
          final String appendStr = s2.trim().toLowerCase();

          if (s.contains(appendStr)) {
            return s;
          }

          if (StrUtil.isNotEmpty(s)) {
            return s + "," + appendStr;
          }

          return appendStr;
        });
  }

  @Data
  public static class UserToAdd {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 2, max = 25, message = "用户名长度在2到25个字符之间")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,}$", message = "用户名只能包含汉字、字母、数字和下划线")
    private String username;

    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    @Length(min = 3, max = 25, message = "密码长度在3到25个字符之间")
    private String password;

    /** 用户所拥有的角色，以{@code ,}分隔，仅支持{@link AuthRole#value()} */
    private String roles;
  }
}
