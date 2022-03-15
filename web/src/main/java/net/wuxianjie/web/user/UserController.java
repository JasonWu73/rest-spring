package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingData;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.security.Admin;
import net.wuxianjie.springbootcore.security.AuthenticationFacade;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.shared.BadRequestException;
import net.wuxianjie.springbootcore.shared.StrUtils;
import net.wuxianjie.springbootcore.shared.Wrote2Db;
import net.wuxianjie.springbootcore.validator.group.Add;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 用户管理。
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

  private final UserService userService;
  private final AuthenticationFacade authenticationFacade;

  /**
   * 获取用户列表。
   */
  @Admin
  @GetMapping("list")
  public PagingData<List<ListOfUserItem>> getUsers(
      @Validated PagingQuery paging,
      @Validated GetUserQuery query) {
    setFuzzySearchValue(query);

    return userService.getUsers(paging, query);
  }

  /**
   * 新增用户。
   */
  @Admin
  @PostMapping("add")
  public Wrote2Db addNewUser(@RequestBody @Validated(Add.class)
                                 AddOrUpdateUserQuery query) {
    setRoleAfterDeduplication(query);

    return userService.addNewUser(query);
  }

  /**
   * 修改用户。
   *
   * <p>注意：此处重置密码无需校验旧密码。</p>
   */
  @Admin
  @PostMapping("update/{userId:\\d+}")
  public Wrote2Db updateUser(@PathVariable("userId") int id,
                             @RequestBody @Validated
                                 AddOrUpdateUserQuery query) {
    query.setUserId(id);

    setRoleAfterDeduplication(query);

    return userService.updateUser(query);
  }

  /**
   * 修改当前用户密码。
   */
  @PostMapping("password")
  public Wrote2Db updateCurrentUserPassword(@RequestBody
                                            @Validated
                                                UpdatePasswordQuery query) {
    validatePasswordDifference(query.getOldPassword(), query.getNewPassword());

    setCurrentUserId(query);

    return userService.updateUserPassword(query);
  }

  /**
   * 删除用户。
   */
  @Admin
  @GetMapping("del/{userId:\\d+}")
  public Wrote2Db deleteUser(@PathVariable("userId") int id) {
    return userService.deleteUser(id);
  }

  private void setFuzzySearchValue(GetUserQuery query) {
    query.setUsername(StrUtils.generateDbFuzzyStr(query.getUsername()));
  }

  private void setRoleAfterDeduplication(AddOrUpdateUserQuery query) {
    final String commaSeparatedStr =
        toDeduplicatedCommaSeparatedLowerCase(query.getRoles());

    validateRole(commaSeparatedStr);

    query.setRoles(commaSeparatedStr);
  }

  private String toDeduplicatedCommaSeparatedLowerCase(String commaSepStr) {
    if (StrUtil.isEmpty(commaSepStr)) {
      return commaSepStr;
    }

    return Arrays.stream(commaSepStr.split(","))
        .reduce("", (s1, s2) -> {
          final String trimmedLowerCase = s2.trim().toLowerCase();

          if (s1.contains(trimmedLowerCase)) {
            return s1;
          }

          if (StrUtil.isNotEmpty(s1)) {
            return s1 + "," + trimmedLowerCase;
          }

          return trimmedLowerCase;
        });
  }

  private void validateRole(String comaSeparatedStr) {
    if (StrUtil.isEmpty(comaSeparatedStr)) {
      return;
    }

    final String[] roles = comaSeparatedStr.split(",");

    final boolean hasInvalidRole = Arrays.stream(roles)
        .anyMatch(x -> Role.resolve(x).isEmpty());

    if (hasInvalidRole) {
      throw new BadRequestException("包含未定义角色");
    }
  }

  private void validatePasswordDifference(String oldPassword,
                                          String newPassword) {
    if (Objects.equals(oldPassword, newPassword)) {
      throw new BadRequestException("新旧密码不能相同");
    }
  }

  private void setCurrentUserId(UpdatePasswordQuery query) {
    final TokenUserDetails userDetails = authenticationFacade.getCurrentUser();

    query.setUserId(userDetails.getAccountId());
  }
}
