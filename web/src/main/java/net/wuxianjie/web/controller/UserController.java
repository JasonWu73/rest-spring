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
  @Admin
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
   * @return 新增操作执行的情况
   */
  @Admin
  @PostMapping("/user")
  public WroteDb saveUser(@Valid @RequestBody final UserToAdd userToAdd) {
    final String roles = toDistinctAndDeduplicateAndLowerCaseRoles(userToAdd.getRoles());
    validateRoles(roles);
    userToAdd.setRoles(roles);
    return userService.saveUser(userToAdd);
  }

  /**
   * 更新用户
   *
   * <p>注意：此处重置密码无需校验旧密码</p>
   *
   * @param userId 需要更新的用户ID
   * @param userToUpdate 用户的最新数据
   * @return 更新操作执行的情况
   */
  @Admin
  @PutMapping("/user/{userId:\\d+}")
  public WroteDb updateUser(@PathVariable final int userId,
                            @Valid @RequestBody final UserToUpdate userToUpdate){
    final String roleStr = toDistinctAndDeduplicateAndLowerCaseRoles(userToUpdate.getRoles());
    validateRoles(roleStr);
    userToUpdate.setUserId(userId);
    userToUpdate.setRoles(roleStr);
    return userService.updateUser(userToUpdate);
  }

  /**
   * 删除用户
   *
   * @param userId 需要删除的用户ID
   * @return 删除操作执行的情况
   */
  @Admin
  @DeleteMapping("/user/{userId:\\d+}")
  public WroteDb removeUser(@PathVariable final int userId) {
    return userService.removeUser(userId);
  }

  @Data
  public static class UserToAdd {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    @Length(min = 2, max = 25, message = "用户名长度需在2到25个字符之间")
    @Pattern(regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,}$", message = "用户名只能包含汉字、字母、数字和下划线")
    private String username;

    /** 登录密码 */
    @NotBlank(message = "密码不能为空")
    @Length(min = 3, max = 25, message = "密码长度需在3到25个字符之间")
    private String password;

    /** 用户所拥有的角色，以{@code ,}分隔，仅支持{@link AuthRole#value()} */
    private String roles;
  }

  /**
   * 最新的用户数据。若某字段值为null，则代表该字段无需更新
   */
  @Data
  public static class UserToUpdate {

    /** 需要更新的用户ID */
    private int userId;

    /** 重置后的新密码，不传或null则代表不重置密码 */
    @Length(min = 3, max = 25, message = "密码长度需在3到25个字符之间")
    private String password;

    /** 用户更新后所拥有的角色，以{@code ,}分隔，仅支持{@link AuthRole#value()} */
    private String roles;
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
}
