package net.wuxianjie.web.service.impl;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.DataConflictException;
import net.wuxianjie.core.model.PaginationData;
import net.wuxianjie.web.controller.UserController;
import net.wuxianjie.web.mapper.UserMapper;
import net.wuxianjie.web.model.User;
import net.wuxianjie.web.model.WroteDb;
import net.wuxianjie.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  private final PasswordEncoder passwordEncoder;

  @Override
  public PaginationData<List<User>> loadUsers(@NotNull final Integer pageNo, @NotNull final Integer pageSize, final String fuzzyUsername) {
    final List<User> users = userMapper.findUsersPaginationByUsername(pageNo * pageSize, pageSize, fuzzyUsername);
    final int total = userMapper.findUserCountByUsername(fuzzyUsername);
    return new PaginationData<>(total, pageNo, pageSize, users);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public WroteDb saveUser(@NotNull final UserController.UserToAdd userToAdd) {
    // 若已存在同名用户，则拒绝添加
    final int count = userMapper.findUserCountByUsername(userToAdd.getUsername());

    if (count > 0) {
      throw new DataConflictException("已存在相同用户名");
    }

    // 编码密码
    final String encodedPassword = passwordEncoder.encode(userToAdd.getPassword());
    userToAdd.setPassword(encodedPassword);

    final int addedNum = userMapper.addUser(userToAdd);

    return new WroteDb(addedNum, "新增用户成功");
  }
}
