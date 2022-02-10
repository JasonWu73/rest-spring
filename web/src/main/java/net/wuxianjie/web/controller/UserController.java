package net.wuxianjie.web.controller;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.domain.PaginationData;
import net.wuxianjie.core.util.StringUtils;
import net.wuxianjie.web.domain.User;
import net.wuxianjie.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 系统可登录用户的控制器
 *
 * @author 吴仙杰
 */

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

  private final UserService userService;

  /**
   * 获取用户分页数据
   *
   * @param pageNo 页码，从0开始，非空
   * @param pageSize 每页显示条数，非空
   * @param username 用户名
   * @return 用户分页数据
   */
  @Admin
  @GetMapping
  public PaginationData<List<User>> loadUsers(@RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestParam(required = false) final String username) {
    return userService.loadUsers(pageNo, pageSize, StringUtils.generateDbFuzzyStr(username));
  }
}
