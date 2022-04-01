package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.operationlog.OperationLogData;
import net.wuxianjie.springbootcore.operationlog.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 操作日志持久化保存实现类。
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
}
