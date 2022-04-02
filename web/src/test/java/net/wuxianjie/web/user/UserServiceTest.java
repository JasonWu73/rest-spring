package net.wuxianjie.web.user;

import cn.hutool.core.util.StrUtil;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.shared.exception.BadRequestException;
import net.wuxianjie.springbootcore.shared.exception.ConflictException;
import net.wuxianjie.springbootcore.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author 吴仙杰
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        underTest = new UserService(userMapper, passwordEncoder);
    }

    @Test
    @DisplayName("获取用户列表")
    void canGetUsers() {
        // given
        final PagingQuery paging = new PagingQuery();
        paging.setPageNo(1);
        paging.setPageSize(2);

        final UserQuery query = new UserQuery();

        final int total = 10;
        final List<UserDto> users = List.of(new UserDto(), new UserDto());
        final PagingResult<UserDto> result = new PagingResult<>(paging, total, users);
        given(userMapper.selectUsers(paging, query)).willReturn(users);
        given(userMapper.countUsers(query)).willReturn(total);

        // when
        final PagingResult<UserDto> actual = underTest.getUsers(paging, query);

        // then
        assertThat(actual).isEqualTo(result);
    }

    @Test
    @DisplayName("新增用户")
    void canSaveUser() {
        // given
        final UserQuery query = new UserQuery();
        final String username = "测试用户";
        final int enabled = 1;
        query.setPassword("123");
        query.setUsername(username);
        query.setEnabled(enabled);

        given(userMapper.existsUserByName(username)).willReturn(false);

        // when
        underTest.saveUser(query);

        // then
        final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insertUser(userArgumentCaptor.capture());

        final User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(username);
        assertThat(capturedUser.getEnabled()).isEqualTo(YesOrNo.resolve(enabled).orElseThrow());
    }

    @Test
    @DisplayName("新增用户 - 已存在相同用户名")
    void canNotSaveUserWhenAlreadyHadUsername() {
        // given
        final UserQuery query = new UserQuery();
        final String username = "测试用户";
        final int enabled = 1;
        query.setUsername(username);
        query.setEnabled(enabled);

        given(userMapper.existsUserByName(username)).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.saveUser(query))
                .isInstanceOf(ConflictException.class)
                .hasMessage("已存在相同用户名");
        verify(userMapper, never()).insertUser(any());
    }

    @Test
    @DisplayName("新增用户 - 启用状态不合法")
    void canNotSaveUserWhenEnabledError() {
        // given
        final UserQuery query = new UserQuery();
        final String username = "测试用户";
        final int enabled = 10;
        query.setPassword("123");
        query.setUsername(username);
        query.setEnabled(enabled);

        given(userMapper.existsUserByName(username)).willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.saveUser(query))
                .isInstanceOf(NoSuchElementException.class);
        verify(userMapper, never()).insertUser(any());
    }

    @Test
    @DisplayName("修改用户")
    void canUpdateUser() {
        // given
        final UserQuery query = new UserQuery();
        query.setUserId(1);
        query.setPassword("123");
        query.setRoles(Role.USER.value());
        query.setEnabled(YesOrNo.YES.value());

        final User user = new User();
        user.setUserId(query.getUserId());
        user.setHashedPassword(passwordEncoder.encode("234"));
        user.setRoles(Role.ADMIN.value());
        user.setEnabled(YesOrNo.NO);
        given(userMapper.selectUserById(query.getUserId())).willReturn(user);

        // when
        underTest.updateUser(query);

        // then
        final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateUser(userArgumentCaptor.capture());

        final User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getUserId()).isEqualTo(query.getUserId());
        assertThat(capturedUser.getRoles()).isEqualTo(query.getRoles());
        assertThat(capturedUser.getEnabled().value()).isEqualTo(query.getEnabled());
        assertThat(passwordEncoder.matches(query.getPassword(), capturedUser.getHashedPassword())).isTrue();
    }

    @Test
    @DisplayName("修改用户 - 无更改")
    void canNotUpdateUserWhenNoDataChanged() {
        // given
        final UserQuery query = new UserQuery();
        query.setUserId(1);
        query.setPassword("123");
        query.setRoles(Role.USER.value());
        query.setEnabled(YesOrNo.YES.value());

        final User user = new User();
        user.setUserId(query.getUserId());
        user.setHashedPassword(passwordEncoder.encode(query.getPassword()));
        user.setRoles(query.getRoles());
        user.setEnabled(YesOrNo.resolve(query.getEnabled()).orElseThrow());
        given(userMapper.selectUserById(query.getUserId())).willReturn(user);

        // when
        underTest.updateUser(query);

        // then
        verify(userMapper, never()).updateUser(any());
    }

    @Test
    @DisplayName("修改用户 - 未找到用户")
    void canNotUpdateUserWhenUserNotFound() {
        // given
        final UserQuery query = new UserQuery();
        query.setUserId(1);
        given(userMapper.selectUserById(query.getUserId())).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> underTest.updateUser(query))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(StrUtil.format("未找到 id 为 {} 的用户", query.getUserId()));
        verify(userMapper, never()).updateUser(any());
    }

    @Test
    @DisplayName("修改当前用户密码")
    void canUpdatePassword() {
        // given
        final UserQuery query = new UserQuery();
        query.setUserId(1);
        query.setOldPassword("123");
        query.setNewPassword("234");

        final User user = new User();
        user.setUserId(query.getUserId());
        user.setHashedPassword(passwordEncoder.encode("123"));
        given(userMapper.selectUserById(query.getUserId())).willReturn(user);

        // when
        underTest.updatePassword(query);

        // then
        final ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateUser(userArgumentCaptor.capture());

        final User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getUserId()).isEqualTo(query.getUserId());
        assertThat(passwordEncoder.matches(query.getNewPassword(), capturedUser.getHashedPassword())).isTrue();
    }

    @Test
    @DisplayName("修改当前用户密码 - 未找到用户")
    void canNotUpdatePasswordWhenUserNotFound() {
        // given
        final UserQuery query = new UserQuery();
        query.setUserId(1);
        given(userMapper.selectUserById(query.getUserId())).willReturn(null);

        // when
        // then
        assertThatThrownBy(() -> underTest.updatePassword(query))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(StrUtil.format("未找到 id 为 {} 的用户", query.getUserId()));
        verify(userMapper, never()).updateUser(any());
    }

    @Test
    @DisplayName("修改当前用户密码 - 旧密码错误")
    void canNotUpdatePasswordWhenOldPasswordWrong() {
        // given
        final UserQuery query = new UserQuery();
        query.setUserId(1);
        query.setOldPassword("123");

        final User user = new User();
        user.setUserId(query.getUserId());
        user.setHashedPassword(passwordEncoder.encode("666"));
        given(userMapper.selectUserById(query.getUserId())).willReturn(user);

        // when
        // then
        assertThatThrownBy(() -> underTest.updatePassword(query))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("旧密码错误");
        verify(userMapper, never()).updateUser(any());
    }

    @Test
    @DisplayName("删除用户")
    void canRemoveUser() {
        // given
        final int userId = 1;

        // when
        underTest.removeUser(userId);

        // then
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(userMapper).deleteUserById(integerArgumentCaptor.capture());

        assertThat(integerArgumentCaptor.getValue()).isEqualTo(userId);
    }
}