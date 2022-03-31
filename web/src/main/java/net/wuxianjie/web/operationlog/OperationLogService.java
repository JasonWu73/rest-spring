package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.AuthUtils;
import net.wuxianjie.web.user.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class OperationLogService {

    private final OperationLogMapper logMapper;

    public PagingResult<OperationLogListItemDto> getOperationLogs(PagingQuery paging, GetOperationLogQuery query) {
        List<OperationLogListItemDto> logs = logMapper.findByQueryPagingOrderByOperationTimeDesc(paging, query);
        int total = logMapper.countByQuery(query);
        return new PagingResult<>(paging, total, logs);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNewOperationLog(LocalDateTime operationTime, String message) {
        UserDetails userDetails = (UserDetails) AuthUtils.getCurrentUser().orElseThrow();
        OperationLog logToAdd = new OperationLog(null, operationTime,
                userDetails.getAccountId(), userDetails.getAccountName(), message);
        logMapper.add(logToAdd);
    }
}
