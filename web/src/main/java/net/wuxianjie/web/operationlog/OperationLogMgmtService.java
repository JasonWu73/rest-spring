package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志管理业务逻辑。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class OperationLogMgmtService {

    private final OperationLogMapper logMapper;

    /**
     * 获取操作日志列表。
     *
     * @param paging 分页参数
     * @param query  查询参数
     * @return 日志列表
     */
    public PagingResult<OperationLogDto> getLogs(PagingQuery paging, OperationLogQuery query) {
        final List<OperationLogDto> logs = logMapper.selectLogs(paging, query);

        final int total = logMapper.countLogs(query);

        return new PagingResult<>(paging, total, logs);
    }
}
