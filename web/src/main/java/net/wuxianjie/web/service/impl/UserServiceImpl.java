package net.wuxianjie.web.service.impl;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.domain.PaginationData;
import net.wuxianjie.web.domain.User;
import net.wuxianjie.web.mapper.UserMapper;
import net.wuxianjie.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 实现系统可登录用户的业务操作
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl implements UserService {

  private final UserMapper userMapper;

  @Override
  public PaginationData<List<User>> loadUsers(@NotNull final Integer pageNo, @NotNull final Integer pageSize, final String fuzzyUsername) {
    final List<User> users = userMapper.findUsersPaginationByUsername(pageNo * pageSize, pageSize, fuzzyUsername);
    final int total = userMapper.findUserCount(fuzzyUsername);
    return new PaginationData<>(total, pageNo, pageSize, users);
  }
}
