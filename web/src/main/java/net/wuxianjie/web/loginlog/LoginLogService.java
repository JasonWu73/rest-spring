package net.wuxianjie.web.loginlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 登录日志的业务逻辑处理类。
 *
 * @author 吴仙杰
 */
@Service
@RequiredArgsConstructor
public class LoginLogService {

  private final LoginLogMapper loginLogMapper;

  /**
   * 保存登录日志。
   *
   * @param logData 需要保存的登录日志数据
   */
  @Transactional(rollbackFor = Exception.class)
  public void saveLoginLog(LoginLog logData) {
    loginLogMapper.save(logData);
  }

  /**
   * 获取登录日志列表。
   *
   * @param paging 分页参数
   * @param query  请求参数
   * @return 登录日志列表
   */
  public PagingResult<LoginLog> getLoginLogs(PagingQuery paging, GetLoginLogQuery query) {
    List<LoginLog> logs = loginLogMapper.findByLoginTimeBetweenAndUsernameLikeAndReqIpLikeOrderByLoginTimeDesc(paging, query);
    int total = loginLogMapper.countByLoginTimeBetweenAndUsernameLikeAndReqIpLike(query);
    return new PagingResult<>(paging, total, logs);
  }
}
