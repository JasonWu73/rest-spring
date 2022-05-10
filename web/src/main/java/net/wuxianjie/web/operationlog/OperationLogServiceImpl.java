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
 * 操作日志业务逻辑实现类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

  private final OperationLogMapper logMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void saveLog(OperationLogData logData) {
    logMapper.save(new OperationLog(logData));
  }

  /**
   * 获取操作日志列表。
   *
   * @param paging 分页参数
   * @param query  请求参数
   * @return 操作日志列表
   */
  public PagingResult<LogItemDto> getLogs(PagingQuery paging, GetLogQuery query) {
    List<LogItemDto> logs = logMapper.findByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLikeOrderByOperationTimeDesc(paging, query);

    int total = logMapper.countByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLike(query);

    return new PagingResult<>(paging, total, logs);
  }
}
