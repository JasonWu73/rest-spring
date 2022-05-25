package net.wuxianjie.web.oplog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 操作日志的业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class OpLogService {

  private final OpLogMapper opLogMapper;

  /**
   * 保存操作日志。
   *
   * @param logData 需要保存的操作日志数据
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveOpLog(OpLog logData) {
    opLogMapper.save(logData);
  }

  /**
   * 获取操作日志列表。
   *
   * @param paging 分页参数
   * @param query  请求参数
   * @return 操作日志列表
   */
  public PagingResult<OpLog> getOpLogs(PagingQuery paging, GetOpLogQuery query) {
    List<OpLog> logs = opLogMapper.findByOpTimeBetweenAndUsernameLikeAndReqIpLikeAndMethodMsgLikeOrderByOpTimeDesc(paging, query);
    int total = opLogMapper.countByOpTimeBetweenAndUsernameLikeAndReqIpLikeAndMethodMsgLike(query);
    return new PagingResult<>(paging, total, logs);
  }
}
