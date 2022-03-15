package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingData;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.security.AuthenticationFacade;
import net.wuxianjie.springbootcore.security.TokenUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志。
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogService {

  private final OperationLogMapper logMapper;
  private final AuthenticationFacade authenticationFacade;

  public PagingData<List<OperationLog>> getOperationLogs(
      PagingQuery paging,
      LocalDateTime startTimeInclusive,
      LocalDateTime endTimeInclusive) {
    final List<OperationLog> logs =
        logMapper.findByStartEndTimePagingOperationTimeDesc(paging,
            startTimeInclusive, endTimeInclusive);

    final int total =
        logMapper.countByStartEndTime(startTimeInclusive, endTimeInclusive);

    return new PagingData<>(paging, total, logs);
  }

  @Transactional(rollbackFor = Exception.class)
  public void addNewOperationLog(LocalDateTime operationTime, String message) {
    final TokenUserDetails userDetails = authenticationFacade.getCurrentUser();

    final OperationLog logToAdd = new OperationLog(null,
        operationTime, userDetails.getAccountId(), userDetails.getAccountName(),
        message);

    logMapper.add(logToAdd);
  }
}
