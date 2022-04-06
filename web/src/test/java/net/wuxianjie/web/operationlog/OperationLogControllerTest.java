package net.wuxianjie.web.operationlog;

import cn.hutool.core.date.LocalDateTimeUtil;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.Role;
import net.wuxianjie.springbootcore.security.SecurityConfig;
import net.wuxianjie.springbootcore.security.TokenAuthenticationService;
import net.wuxianjie.web.user.UserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author 吴仙杰
 */
@WebMvcTest(controllers = OperationLogController.class)
class OperationLogControllerTest {

    static final String BEARER_PREFIX = "Bearer ";
    static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    @MockBean
    private OperationLogServiceImpl logService;
    @MockBean
    private TokenAuthenticationService authService;
    @SuppressWarnings("unused")
    @MockBean
    private SecurityConfig securityConfig;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("获取操作日志列表")
    void canGetLogs() throws Exception {
        // given
        final PagingQuery paging = new PagingQuery();
        paging.setPageNo(1);
        paging.setPageSize(2);

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        final int total = 10;
        final List<OperationLogDto> list = List.of(new OperationLogDto(), new OperationLogDto());
        final PagingResult<OperationLogDto> result = new PagingResult<>(paging, total, list);
        given(logService.getLogs(any(), any())).willReturn(result);

        // when
        mockMvc.perform(get("/api/v1/operation-log/list")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .param("pageNo", paging.getPageNo().toString())
                        .param("pageSize", paging.getPageSize().toString())
                        .param("startDate", "2022-04-01")
                        .param("endDate", "2022-04-01"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(0))
                .andExpect(jsonPath("$.data.total").value(total));

        // then
        final ArgumentCaptor<OperationLogQuery> logArgumentCaptor = ArgumentCaptor.forClass(OperationLogQuery.class);
        verify(logService).getLogs(any(), logArgumentCaptor.capture());

        final OperationLogQuery logCaptured = logArgumentCaptor.getValue();
        final LocalDateTime expectedStartTime = LocalDateTimeUtil.parse("2022-04-01 00:00:00", NORM_DATETIME_PATTERN);
        final LocalDateTime expectedEndTime = LocalDateTimeUtil.parse("2022-04-01 23:59:59", NORM_DATETIME_PATTERN);
        assertThat(logCaptured.getStartTimeInclusive()).isEqualToIgnoringNanos(expectedStartTime);
        assertThat(logCaptured.getEndTimeInclusive()).isEqualToIgnoringNanos(expectedEndTime);
    }

    @Test
    @DisplayName("获取操作日志列表 - 开始日期不合法")
    void canNotGetLogsWhenInvalidStartDate() throws Exception {
        // given
        final PagingQuery paging = new PagingQuery();
        paging.setPageNo(1);
        paging.setPageSize(2);

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        final int total = 10;
        final List<OperationLogDto> list = List.of(new OperationLogDto(), new OperationLogDto());
        final PagingResult<OperationLogDto> result = new PagingResult<>(paging, total, list);
        given(logService.getLogs(any(), any())).willReturn(result);

        // when
        mockMvc.perform(get("/api/v1/operation-log/list")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .param("pageNo", paging.getPageNo().toString())
                        .param("pageSize", paging.getPageSize().toString())
                        .param("startDate", "2022-04-91")
                        .param("endDate", "2022-04-01"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("开始日期不合法"));
    }

    @Test
    @DisplayName("获取操作日志列表 - 结束日期不合法")
    void canNotGetLogsWhenInvalidEndDate() throws Exception {
        // given
        final PagingQuery paging = new PagingQuery();
        paging.setPageNo(1);
        paging.setPageSize(2);

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        final int total = 10;
        final List<OperationLogDto> list = List.of(new OperationLogDto(), new OperationLogDto());
        final PagingResult<OperationLogDto> result = new PagingResult<>(paging, total, list);
        given(logService.getLogs(any(), any())).willReturn(result);

        // when
        mockMvc.perform(get("/api/v1/operation-log/list")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .param("pageNo", paging.getPageNo().toString())
                        .param("pageSize", paging.getPageSize().toString())
                        .param("startDate", "2022-04-01")
                        .param("endDate", "2022-04-91"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("结束日期不合法"));
    }

    @Test
    @DisplayName("获取操作日志列表 - 开始日期晚于结束日期")
    void catNotGetLogsWhenStartDateIsAfterEndDate() throws Exception {
        // given
        final PagingQuery paging = new PagingQuery();
        paging.setPageNo(1);
        paging.setPageSize(2);

        final String token = "fake_token";
        final UserDetails userDetails = new UserDetails();
        userDetails.setRoles(Role.ADMIN.value());
        given(authService.authenticate(token)).willReturn(userDetails);

        final int total = 10;
        final List<OperationLogDto> list = List.of(new OperationLogDto(), new OperationLogDto());
        final PagingResult<OperationLogDto> result = new PagingResult<>(paging, total, list);
        given(logService.getLogs(any(), any())).willReturn(result);

        // when
        mockMvc.perform(get("/api/v1/operation-log/list")
                        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                        .param("pageNo", paging.getPageNo().toString())
                        .param("pageSize", paging.getPageSize().toString())
                        .param("startDate", "2022-04-21")
                        .param("endDate", "2022-04-01"))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").value(1))
                .andExpect(jsonPath("$.errMsg").value("开始日期不能晚于结束日期"));
    }
}