package net.wuxianjie.web.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.security.SecurityConfigData;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    static final String BEARER_PREFIX = "Bearer ";
    static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    @MockBean
    private UserService userService;
    @MockBean
    private TokenAuthenticationService authService;
    @SuppressWarnings("unused")
    @MockBean
    private SecurityConfigData configData;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("获取用户列表")
    void canGetUsers() throws Exception {
        // given
        final PagingQuery paging = new PagingQuery();
        paging.setPageNo(1);
        paging.setPageSize(2);

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        final int total = 10;
        final List<UserDto> list = List.of(new UserDto(), new UserDto());
        final PagingResult<UserDto> result = new PagingResult<>(paging, total, list);
        given(userService.getUsers(any(), any())).willReturn(result);

        // when
        mockMvc.perform(get("/api/v1/user/list")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .param("pageNo", paging.getPageNo().toString())
                        .param("pageSize", paging.getPageSize().toString())
                        .param("username", "测试用户"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.data.total").value(total));

        // then
        final ArgumentCaptor<UserQuery> userArgumentCaptor = ArgumentCaptor.forClass(UserQuery.class);
        verify(userService).getUsers(any(), userArgumentCaptor.capture());

        final UserQuery userCaptured = userArgumentCaptor.getValue();
        assertThat(userCaptured.getUsername()).isEqualTo("%测试用户%");
    }

    @Test
    @DisplayName("新增用户")
    void canSaveUser() throws Exception {
        // given
        String roles = "admin,user,admin";
        String expectedRoles = "admin,user";
        final UserQuery userQuery = new UserQuery();
        userQuery.setRoles(roles);
        userQuery.setPassword("123");
        userQuery.setUsername("测试用户");
        userQuery.setEnabled(1);

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(post("/api/v1/user/add")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userQuery)))
                // then
                .andExpect(status().isOk());

        // then
        final ArgumentCaptor<UserQuery> userArgumentCaptor = ArgumentCaptor.forClass(UserQuery.class);
        verify(userService).saveUser(userArgumentCaptor.capture());

        final UserQuery userCaptured = userArgumentCaptor.getValue();
        assertThat(userCaptured.getRoles()).isEqualTo(expectedRoles);
    }

    @Test
    @DisplayName("修改用户")
    void canUpdateUser() throws Exception {
        // given
        String roles = "admin,user,admin";
        String expectedRoles = "admin,user";
        final UserQuery userQuery = new UserQuery();
        userQuery.setRoles(roles);

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(post("/api/v1/user/update/1")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userQuery)))
                // then
                .andExpect(status().isOk());

        // then
        final ArgumentCaptor<UserQuery> userArgumentCaptor = ArgumentCaptor.forClass(UserQuery.class);
        verify(userService).updateUser(userArgumentCaptor.capture());

        final UserQuery userCaptured = userArgumentCaptor.getValue();
        assertThat(userCaptured.getRoles()).isEqualTo(expectedRoles);

        assertThat(userCaptured.getUserId()).isEqualTo(1);
    }

    @Test
    @DisplayName("修改当前用户密码")
    void canUpdatePassword() throws Exception {
        // given
        final UserQuery userQuery = new UserQuery();
        userQuery.setOldPassword("123");
        userQuery.setNewPassword("234");

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountId(100);
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(post("/api/v1/user/password")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userQuery)))
                // then
                .andExpect(status().isOk());

        // then
        final ArgumentCaptor<UserQuery> userArgumentCaptor = ArgumentCaptor.forClass(UserQuery.class);
        verify(userService).updatePassword(userArgumentCaptor.capture());

        final UserQuery userCaptured = userArgumentCaptor.getValue();
        assertThat(userCaptured.getUserId()).isEqualTo(userDetails.getAccountId());
    }

    @Test
    @DisplayName("修改当前用户密码失败 - 新旧密码相同")
    void canNotUpdatePasswordWhenNewAndOldPasswordIsSame() throws Exception {
        // given
        final UserQuery userQuery = new UserQuery();
        userQuery.setOldPassword("123");
        userQuery.setNewPassword("123");

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setAccountId(100);
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(post("/api/v1/user/password")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userQuery)))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errMsg").value("新旧密码不能相同"));

        // then
        verify(userService, never()).updatePassword(any());
    }

    @Test
    @DisplayName("删除用户")
    void removeUser() throws Exception {
        // given
        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        // when
        mockMvc.perform(get("/api/v1/user/del/1")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token))
                // then
                .andExpect(status().isOk());

        // then
        final ArgumentCaptor<Integer> intArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(userService).removeUser(intArgumentCaptor.capture());

        assertThat(intArgumentCaptor.getValue()).isEqualTo(1);
    }
}