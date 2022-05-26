package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import net.wuxianjie.springbootcore.util.StrUtils;
import net.wuxianjie.web.oplog.OpLogger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * 用户管理的 API 控制器。
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
   * @param query  请求参数
   * @return 用户列表
   */
  @GetMapping("list")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_LIST.name())")
  public PagingResult<UserDto> getUsers(@Valid PagingQuery paging,
                                        @Valid GetUserQuery query) {
    setFuzzySearchValue(query);
    return userService.getUsers(paging, query);
  }

  /**
   * 新增用户。
   *
   * @param query 需要保存的用户数据
   * @return 新增成功后的提示信息
   */
  @PostMapping("add")
  @OpLogger("新增用户")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_ADD.name())")
  public Map<String, String> saveUser(@RequestBody @Valid SaveUserQuery query) {
    return userService.saveUser(query);
  }

  /**
   * 修改用户。
   *
   * @param userId 用户 id
   * @param query  需要更新的用户数据
   * @return 修改成功后的提示信息
   */
  @PostMapping("update/{userId:\\d+}")
  @OpLogger("修改用户")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_UPDATE.name())")
  public Map<String, String> updateUser(@PathVariable int userId,
                         @RequestBody @Valid UpdateUserQuery query) {
    query.setUserId(userId);
    return userService.updateUser(query);
  }

  /**
   * 重置用户密码。
   *
   * @param userId 用户 id
   * @param query  需要更新的用户数据
   * @return 密码重置成功后的提示信息
   */
  @PostMapping("reset-pwd/{userId:\\d+}")
  @OpLogger("修改用户")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_RESET_PWD.name())")
  public Map<String, String> updateUserPwd(@PathVariable int userId,
                                           @RequestBody @Valid ResetUserPwdQuery query) {
    query.setUserId(userId);
    return userService.updateUserPwd(query);
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 新旧密码数据
   * @return 密码修改成功后的提示信息
   */
  @PostMapping("chg-pwd")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_RESET_PWD.name())")
  public Map<String, String> updateUserPwd(@RequestBody @Valid UpdateUserPwdQuery query) {
    if (StrUtil.equals(query.getOldPassword(), query.getNewPassword())) throw new BadRequestException("新旧密码不能相同");

    setCurrentUserId(query);
    return userService.updateUserPwd(query);
  }

  /**
   * 删除用户。
   *
   * @param userId 需要删除的用户 id
   * @return 删除成功后的提示信息
   */
  @GetMapping("del/{userId:\\d+}")
  @OpLogger("删除用户")
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_DEL.name())")
  public Map<String, String> deleteUser(@PathVariable int userId) {
    return userService.deleteUser(userId);
  }

  private void setFuzzySearchValue(GetUserQuery query) {
    query.setUsername(StrUtils.toFuzzy(query.getUsername()));
  }


  private void setCurrentUserId(UpdateUserPwdQuery query) {
    TokenUserDetails user = AuthUtils.getCurrentUser().orElseThrow();
    query.setUserId(user.getUserId());
  }
}
