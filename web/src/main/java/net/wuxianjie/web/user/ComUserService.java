package net.wuxianjie.web.user;

import cn.hutool.core.text.StrSplitter;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import net.wuxianjie.web.security.SysMenu;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户的通用业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class ComUserService {

  private final UserMapper userMapper;

  /**
   * 获取用户数据。
   *
   * @param username 用户名
   * @return 指定用户名的用户数据
   * @throws NotFoundException 当数据库中找不到指定用户名时抛出
   */
  public User getUserFromDbMustBeExists(String username) throws NotFoundException {
    return Optional.ofNullable(userMapper.findByUsername(username))
      .orElseThrow(() -> new NotFoundException("未找到用户（" + username + "）"));
  }

  /**
   * 对英文逗号分隔的菜单编号字符串进行去重和校验后，再返回去重后的菜单编号字符串。
   *
   * @param menus 以英文逗号分隔的菜单编号字符串
   * @return 以英文逗号分隔的菜单编号字符串的 {@link Optional} 包装对象
   */
  public Optional<String> toDeduplicatedCommaSeparatedMenus(String menus) {
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

}
