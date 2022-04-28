package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.operationlog.OperationLogger;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.Admin;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.util.StrUtils;
import net.wuxianjie.springbootcore.validator.group.GroupOne;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Optional;

/**
 * 用户管理 API 控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  /**
   * 获取用户列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 用户列表
   */
  @Admin
  @GetMapping("list")
  public PagingResult<UserItemDto> getUsers(@Valid PagingQuery paging,
                                            @Valid GetUserQuery query) {
    setFuzzySearchValue(query);

    return userService.getUsers(paging, query);
  }

  /**
   * 新增用户。
   *
   * @param query 需要保存的用户数据
   */
  @Admin
  @OperationLogger("新增用户")
  @PostMapping("add")
  public void saveUser(@RequestBody @Validated(GroupOne.class) SaveOrUpdateUserQuery query) {
    setRoleAfterDeduplication(query);

    userService.saveUser(query);
  }

  /**
   * 修改用户。
   * <p>
   * 注意：此处为重置密码，即无需验证旧密码。
   * </p>
   *
   * @param userId 用户 id
   * @param query  需要更新的用户数据
   */
  @Admin
  @OperationLogger("修改用户")
  @PostMapping("update/{userId:\\d+}")
  public void updateUser(@PathVariable int userId,
                         @RequestBody @Validated SaveOrUpdateUserQuery query) {
    query.setUserId(userId);

    setRoleAfterDeduplication(query);

    userService.updateUser(query);
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 新旧密码数据
   */
  @PostMapping("password")
  public void updatePassword(@RequestBody @Valid PasswordQuery query) {
    if (StrUtil.equals(query.getOldPassword(), query.getNewPassword())) {
      throw new BadRequestException("新旧密码不能相同");
    }

    setCurrentUserId(query);

    userService.updatePassword(query);
  }

  /**
   * 删除用户。
   *
   * @param userId 用户 id
   * @param query  需要删除的用户
   */
  @Admin
  @OperationLogger("删除用户")
  @GetMapping("del/{userId:\\d+}")
  public void deleteUser(@PathVariable int userId,
                         DelUserQuery query) {
    query.setUserId(userId);

    userService.deleteUser(query);
  }

  private void setFuzzySearchValue(GetUserQuery query) {
    query.setUsername(StrUtils.toFuzzy(query.getUsername()));
  }

  private void setRoleAfterDeduplication(SaveOrUpdateUserQuery query) {
    toDeduplicatedCommaSeparatedLowerCase(query.getRoles())
      .ifPresent(s -> {
        verifyRole(s);

        query.setRoles(s);
      });
  }

  private Optional<String> toDeduplicatedCommaSeparatedLowerCase(String roles) {
    return Optional.ofNullable(StrUtil.trimToNull(roles))
      .map(s -> Arrays.stream(roles.split(","))
        .reduce("", (roleOne, roleTwo) -> {
          String appended = roleTwo.trim().toLowerCase();

          if (StrUtil.isEmpty(roleOne)) {
            return appended;
          }

          if (roleOne.contains(appended)) {
            return roleOne;
          }

          return roleOne + "," + appended;
        }));
  }

  private void verifyRole(String roles) {
    if (StrUtil.isEmpty(roles)) {
      return;
    }

    boolean hasInvalidRole = Arrays.stream(roles.split(","))
      .anyMatch(s -> Role.resolve(s).isEmpty());

    if (hasInvalidRole) {
      throw new BadRequestException("包含未定义角色");
    }
  }

  private void setCurrentUserId(PasswordQuery query) {
    UserDetails user = (UserDetails) AuthUtils.getCurrentUser().orElseThrow();

    query.setUserId(user.getAccountId());
    query.setUsername(user.getAccountName());
  }
}
