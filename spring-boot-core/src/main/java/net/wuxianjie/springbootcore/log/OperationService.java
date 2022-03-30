package net.wuxianjie.springbootcore.log;

/**
 * 操作日志持久化保存接口。
 *
 * @author 吴仙杰
 */
public interface OperationService {

    /**
     * 保存操作日志数据。
     *
     * @param log 日志数据
     */
    void saveLog(OperationLog log);
}
