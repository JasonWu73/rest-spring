package net.wuxianjie.web.operationlog;

import net.wuxianjie.springbootcore.operationlog.OperationLogData;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author 吴仙杰
 */
@ExtendWith(MockitoExtension.class)
class OperationLogServiceImplTest {

    @Mock
    private OperationLogMapper logMapper;

    private OperationLogServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new OperationLogServiceImpl(logMapper);
    }

    @Test
    @DisplayName("保存操作日志")
    void canSaveLog() {
        // given
        final OperationLogData logData = new OperationLogData();
        final String methodMsg = "测试方法";
        logData.setMethodMessage(methodMsg);

        // when
        underTest.saveLog(logData);

        // then
        final ArgumentCaptor<OperationLog> logArgumentCaptor = ArgumentCaptor.forClass(OperationLog.class);
        verify(logMapper).insertLog(logArgumentCaptor.capture());

        final OperationLog capturedLog = logArgumentCaptor.getValue();
        assertThat(capturedLog.getMethodMessage()).isEqualTo(methodMsg);
    }

    @Test
    @DisplayName("获取操作日志列表")
    void canGetLogs() {
        // given
        final PagingQuery paging = new PagingQuery();
        paging.setPageNo(1);
        paging.setPageSize(2);

        final OperationLogQuery query = new OperationLogQuery();
        query.setMethodMessage("测试方法");

        final int total = 10;
        final List<OperationLogDto> logs = List.of(new OperationLogDto(), new OperationLogDto());
        final PagingResult<OperationLogDto> result = new PagingResult<>(paging, total, logs);
        given(logMapper.selectLogs(paging, query)).willReturn(logs);
        given(logMapper.countLogs(query)).willReturn(total);

        // when
        final PagingResult<OperationLogDto> actual = underTest.getLogs(paging, query);

        // then
        assertThat(actual).isEqualTo(result);
    }
}