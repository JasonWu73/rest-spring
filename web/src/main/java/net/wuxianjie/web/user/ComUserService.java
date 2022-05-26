package net.wuxianjie.web.user;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
