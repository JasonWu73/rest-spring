package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.paging.RequestOfPaging;
import net.wuxianjie.springbootcore.paging.ResultOfPaging;
import net.wuxianjie.springbootcore.security.AuthenticationUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.util.StringUtils;
import net.wuxianjie.web.operationlog.OperationLogger;
import net.wuxianjie.web.shared.SimpleResultOfWriteOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户 API 控制器。
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
  @GetMapping("list")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_USER.name())")
  public ResultOfPaging<ListItemOfUser> getUsers(@Valid RequestOfPaging paging,
                                                 @Valid RequestOfGetUser query) {
    setFuzzySearchValue(query);
    return userService.getUsers(paging, query);
  }

  /**
   * 新增用户。
   *
   * @param query 需要保存的用户数据
   * @return 操作结果
   */
  @PostMapping("add")
  @OperationLogger("新增用户")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_USER_ADD.name())")
  public SimpleResultOfWriteOperation saveUser(@RequestBody @Valid RequestOfSaveUser query) {
    return userService.saveUser(query);
  }

  /**
   * 修改用户。
   *
   * @param userId 用户 id
   * @param query  需要更新的用户数据
   * @return 操作结果
   */
  @PostMapping("update/{userId:\\d+}")
  @OperationLogger("修改用户")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_USER_UPDATE.name())")
  public SimpleResultOfWriteOperation updateUser(@PathVariable int userId,
                                                 @RequestBody @Valid RequestOfUpdateUser query) {
    query.setUserId(userId);
    return userService.updateUser(query);
  }

  /**
   * 重置用户密码。
   *
   * @param userId 用户 id
   * @param query  需要更新的用户数据
   * @return 操作结果
   */
  @PostMapping("reset-password/{userId:\\d+}")
  @OperationLogger("重置用户密码")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_USER_RESET_PWD.name())")
  public SimpleResultOfWriteOperation updateUserPassword(@PathVariable int userId,
                                                         @RequestBody @Valid RequestOfResetUserPassword query) {
    query.setUserId(userId);
    return userService.updateUserPassword(query);
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 新旧密码数据
   * @return 操作结果
   */
  @PostMapping("change-password")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_USER_RESET_PWD.name())")
  public SimpleResultOfWriteOperation updateCurrentUserPassword(@RequestBody @Valid RequestOfUpdateUserPwd query) {
    if (StrUtil.equals(query.getOldPassword(), query.getNewPassword())) throw new BadRequestException("新旧密码不能相同");

    setCurrentUserId(query);
    return userService.updateCurrentUserPassword(query);
  }

  /**
   * 删除用户。
   *
   * @param userId 需要删除的用户 id
   * @return 操作结果
   */
  @GetMapping("del/{userId:\\d+}")
  @OperationLogger("删除用户")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.RoleOfMenu).ROLE_USER_DEL.name())")
  public SimpleResultOfWriteOperation deleteUser(@PathVariable int userId) {
    return userService.deleteUser(userId);
  }

  private void setFuzzySearchValue(RequestOfGetUser query) {
    query.setUsername(StringUtils.toNullableFuzzyString(query.getUsername()));
  }


  private void setCurrentUserId(RequestOfUpdateUserPwd query) {
    TokenUserDetails user = AuthenticationUtils.getCurrentUser().orElseThrow();
    query.setUserId(user.getUserId());
  }
}
