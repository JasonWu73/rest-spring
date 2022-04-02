package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.operationlog.OperationLogData;
import net.wuxianjie.springbootcore.operationlog.OperationLogService;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper logMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(final OperationLogData logData) {
        logMapper.insertLog(new OperationLog(logData));
    }

    /**
     * 获取操作日志列表。
     *
     * @param paging 分页参数
     * @param query  查询参数
     * @return 日志列表
     */
    public PagingResult<OperationLogDto> getLogs(final PagingQuery paging,
                                                 final OperationLogQuery query) {
        final List<OperationLogDto> logs = logMapper.selectLogs(paging, query);

        final int total = logMapper.countLogs(query);

        return new PagingResult<>(paging, total, logs);
    }
}
