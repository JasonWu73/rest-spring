package net.wuxianjie.web.user;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.springbootcore.util.StrUtils;
import net.wuxianjie.web.oplog.OpLogger;
import net.wuxianjie.web.security.SysMenu;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER.name())")
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
    toDeduplicatedCommaSeparatedMenus(query.getMenus()).ifPresent(query::setMenus);
    return userService.saveUser(query);
  }

  /**
   * 修改用户。
   *
   * @param userId 用户 id
   * @param query  需要更新的用户数据
   * @return 修改成功后的提示信息
   */
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_UPDATE.name())")
  @OpLogger("修改用户")
  @PostMapping("update/{userId:\\d+}")
  public Map<String, String> updateUser(@PathVariable int userId,
                         @RequestBody @Valid UpdateUserQuery query) {
    query.setUserId(userId);
    toDeduplicatedCommaSeparatedMenus(query.getMenus()).ifPresent(query::setMenus);
    return userService.updateUser(query);
  }

  /**
   * 修改当前用户密码。
   *
   * @param query 新旧密码数据
   */
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_RESET_PWD.name())")
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
  @PreAuthorize("hasRole(T(net.wuxianjie.web.security.SysMenu).ROLE_USER_DEL.name())")
  @OpLogger("删除用户")
  @GetMapping("del/{userId:\\d+}")
  public void deleteUser(@PathVariable int userId,
                         LogOfDelUserQuery query) {
    query.setUserId(userId);

    userService.deleteUser(query);
  }

  private void setFuzzySearchValue(GetUserQuery query) {
    query.setUsername(StrUtils.toFuzzy(query.getUsername()));
  }

  private Optional<String> toDeduplicatedCommaSeparatedMenus(String menus) {
    return Optional.ofNullable(StrUtil.trimToNull(menus))
      .flatMap(notNullMenus -> {
        String[] menusArray = StrSplitter.splitToArray(notNullMenus, ',', 0, true, true);

        if (menusArray.length == 0) return Optional.empty();

        boolean hasAnyInvalidMenu = Arrays.stream(menusArray)
          .anyMatch(menu -> SysMenu.resolve(menu).isEmpty());

        if (hasAnyInvalidMenu) throw new BadRequestException("包含非法菜单编号");

        return Optional.of(Arrays.stream(menusArray)
          .distinct()
          .collect(Collectors.joining(",")));
      });
  }

  private void setCurrentUserId(PasswordQuery query) {
    UserDetails user = (UserDetails) AuthUtils.getCurrentUser().orElseThrow();

    query.setUserId(user.getAccountId());
    query.setUsername(user.getAccountName());
  }
}
