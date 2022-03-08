package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.paging.PagingData;
import net.wuxianjie.core.paging.PagingQuery;
import net.wuxianjie.core.security.AuthenticationFacade;
import net.wuxianjie.core.security.TokenUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogService {

    private final OperationLogMapper logMapper;
    private final AuthenticationFacade authenticationFacade;

    public PagingData<List<ListItemOfOperationLog>> getOperationLogs(
            PagingQuery paging,
            LocalDateTime startTimeInclusive,
            LocalDateTime endTimeInclusive
    ) {
        List<OperationLog> logs = logMapper.findByStartEndTimeLimitTimeDesc(
                paging,
                startTimeInclusive,
                endTimeInclusive
        );

        int total = logMapper.countByStartEndTime(startTimeInclusive, endTimeInclusive);

        List<ListItemOfOperationLog> logList = logs.stream()
                .map(ListItemOfOperationLog::new)
                .collect(Collectors.toList());

        return new PagingData<>(total, paging.getPageNo(), paging.getPageSize(), logList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addNewOperationLog(LocalDateTime operationTime, String message) {
        TokenUserDetails userDetails = authenticationFacade.getCurrentLoggedInUserDetails();

        OperationLog logToAdd = new OperationLog(
                null,
                operationTime,
                userDetails.getAccountId(),
                userDetails.getAccountName(),
                message
        );

        logMapper.add(logToAdd);
    }
}
