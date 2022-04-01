package net.wuxianjie.web.operationlog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest(controllers = LogMgmtController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class OperationLogMgmtControllerTest {

    static final String BEARER_PREFIX = "Bearer ";
    static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("获取操作日志列表")
    void canGetLogs() throws Exception {
        // given
        final String pageNo = "1";
        final String pageSize = "2";

        // when
        mockMvc.perform(get("/api/v1/opr-log/list"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print());
    }
}