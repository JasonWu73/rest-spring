package net.wuxianjie.web.controller;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.constant.AuthRole;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.model.CachedToken;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.core.model.PaginationQuery;
import net.wuxianjie.core.service.AuthenticationFacade;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.model.Wrote2Database;
import net.wuxianjie.web.service.UserService;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Arrays;
import java.util.List;

/**
 * 用户管理REST API控制器
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

  private final UserService userService;
  private final AuthenticationFacade authentication;

  /**
   * 根据分页条件及用户名获取用户列表数据
   *
   * @param pagination 分页条件，必填
   * @param username 用户名，空或不传都代表不使用该查询条件
   * @return 用户分页数据
   */
  @Admin
  @GetMapping("list")
  public PaginationData<List<User>> getUsers(
    @Valid final PaginationQuery pagination, final String username) {
    // 完善分页条件
    pagination.setOffset();

    // 获取支持数字库LIKE的模糊查询值
    final String fuzzyUsername = StringUtils.generateDbFuzzyStr(username);

    // 根据分页条件及用户名获取操作日志列表数据
    return userService.getUsers(pagination, fuzzyUsername);
  }

  /**
   * 新增用户
   *
   * @param userToAdd 需要新增的用户数据，必填
   * @return 数据库的写入情况及说明
   */
  @Admin
  @PostMapping("add")
  public Wrote2Database saveUser(@RequestBody @Valid final UserToAdd userToAdd) {
    // 处理角色字符串：去重、转小写，再以英文逗号作分隔符拼接为符合要求的角色字符串
    final String roles = toDeduplicateLowerCaseRoles(userToAdd.getRoles());
    userToAdd.setRoles(roles);

    // 若角色字符串中包含未定义的角色，则直接退出
    validateRoles(roles);

    // 新增用户数据
    return userService.saveUser(userToAdd);
  }

  /**
   * 更新用户
   *
   * <p>注意：此处重置密码无需校验旧密码</p>
   *
   * @param userId 需要更新的用户ID，必填
   * @param userToUpdate 用户的最新数据，必填
   * @return 数据库的写入情况及说明
   */
  @Admin
  @PostMapping("update/{userId:\\d+}")
  public Wrote2Database updateUser(
    @PathVariable final int userId, @RequestBody @Valid final UserToUpdate userToUpdate){
    // 完善更新参数
    userToUpdate.setUserId(userId);

    // 处理角色字符串：去重、转小写，再以英文逗号作分隔符拼接为符合要求的角色字符串
    final String roleStr = toDeduplicateLowerCaseRoles(userToUpdate.getRoles());
    userToUpdate.setRoles(roleStr);

    // 若角色字符串中包含未定义的角色，则直接退出
    validateRoles(roleStr);

    // 更新用户数据
    return userService.updateUser(userToUpdate);
  }

  /**
   * 修改当前用户的密码
   *
   * @param passwordToUpdate 需要更新的密码，必填
   * @return 数据库的写入情况及说明
   */
  @PostMapping("password")
  public Wrote2Database updatePassword(@RequestBody @Valid final PasswordToUpdate passwordToUpdate){
    // 获取当前登录用户
    final CachedToken cacheToken = authentication.getCacheToken();

    // 完善修改密码参数
    passwordToUpdate.setUserId(cacheToken.getAccountId());

    // 若传入的新旧密码相同，则直接退出
    if (passwordToUpdate.getOldPassword().equals(passwordToUpdate.getNewPassword())) {
      throw new BadRequestException("新旧密码不能相同");
    }

    // 更新用户密码
    return userService.updatePassword(passwordToUpdate);
  }

  /**
   * 删除用户
   *
   * @param userId 需要删除的用户ID，必填
   * @return 删除操作的执行情况
   */
  @Admin
  @GetMapping("del/{userId:\\d+}")
  public Wrote2Database removeUser(@PathVariable final int userId) {
    return userService.removeUser(userId);
  }

  /**
   * 要向数据库中新增的用户数据
   */
  @Data
  public static class UserToAdd {

    /** 用户名，必填，长度需在2到25个字符之间，且只能包含汉字、字母、数字和下划线 */
    @NotBlank(message = "用户名不能为空")
    @Length(message = "用户名长度需在2到25个字符之间", min = 2, max = 25)
    @Pattern(message = "用户名只能包含汉字、字母、数字和下划线",
      regexp = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,}$")
    private String username;

    /** 登录密码，必填，长度需在3到25个字符之间 */
    @NotBlank(message = "密码不能为空")
    @Length(message = "密码长度需在3到25个字符之间", min = 3, max = 25)
    private String password;

    /** 用户所拥有的角色，以英文逗号分隔，只能包含user或admin */
    @Pattern(message = "角色只能包含user或admin，且必须以英文逗号分隔",
      regexp = "^(admin|user)(admin|user|,)*$")
    private String roles;
  }

  /**
   * 最新的用户数据。若某字段值为null，则代表该字段无需更新
   */
  @Data
  public static class UserToUpdate {

    /** 需要更新的用户ID */
    private int userId;

    /** 重置后的新密码，长度需在3到25个字符之间，不传或null则代表不重置密码 */
    @Length(message = "密码长度需在3到25个字符之间", min = 3, max = 25)
    private String password;

    /** 用户所拥有的角色，以英文逗号分隔，只能包含user或admin，不传或null则代表不修改用户角色 */
    @Pattern(message = "角色只能包含user或admin，且必须以英文逗号分隔",
      regexp = "^(admin|user)(admin|user|,)*$")
    private String roles;
  }

  @Data
  public static class PasswordToUpdate {

    /** 需要修改密码的用户ID */
    private int userId;

    /** 旧密码，必填，长度需在3到25个字符之间 */
    @NotBlank(message = "旧密码不能为空")
    @Length(message = "密码长度需在3到25个字符之间", min = 3, max = 25)
    private String oldPassword;

    /** 新密码，必填，长度需在3到25个字符之间 */
    @NotBlank(message = "新密码不能为空")
    @Length(message = "密码长度需在3到25个字符之间", min = 3, max = 25)
    private String newPassword;
  }

  private void validateRoles(final String roles) {
    if (StrUtil.isEmpty(roles)) {
      return;
    }

    final boolean hasInvalidRole = Arrays.stream(roles.split(","))
      .anyMatch(x -> AuthRole.resolve(x) == null);

    if (hasInvalidRole) {
      throw new BadRequestException("包含未定义角色");
    }
  }

  private String toDeduplicateLowerCaseRoles(final String roles) {
    if (StrUtil.isEmpty(roles)) {
      return roles;
    }

    return Arrays.stream(roles.split(","))
      .reduce("", (s, s2) -> {
        final String appended = s2.trim().toLowerCase();

        if (s.contains(appended)) {
          return s;
        }

        if (StrUtil.isNotEmpty(s)) {
          return s + "," + appended;
        }

        return appended;
      });
  }
}
