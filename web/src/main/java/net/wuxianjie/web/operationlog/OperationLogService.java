package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.RequestOfPaging;
import net.wuxianjie.springbootcore.paging.ResultOfPaging;
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
public class OperationLogService {

  private final OperationLogMapper operationLogMapper;

  /**
   * 保存操作日志。
   *
   * @param logData 需要保存的操作日志数据
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveOpLog(OperationLog logData) {
    operationLogMapper.save(logData);
  }

  /**
   * 获取操作日志列表。
   *
   * @param paging 分页参数
   * @param query  查询参数
   * @return 操作日志列表
   */
  public ResultOfPaging<OperationLog> getOpLogs(RequestOfPaging paging, RequestOfGetOperationLog query) {
    List<OperationLog> logs = operationLogMapper.findByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLikeOrderByOpTimeDesc(paging, query);
    int total = operationLogMapper.countByOperationTimeBetweenAndUsernameLikeAndRequestIpLikeAndMethodMessageLike(query);
    return new ResultOfPaging<>(paging, total, logs);
  }
}
