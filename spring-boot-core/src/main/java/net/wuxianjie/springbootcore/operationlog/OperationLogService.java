package net.wuxianjie.springbootcore.operationlog;

/**
 * 操作日志持久化保存接口。
 *
 * @author 吴仙杰
 */
public interface OperationLogService {

    /**
     * 保存操作日志数据。
     *
     * @param logData 日志数据
     */
    void saveLog(OperationLogData logData);
}
