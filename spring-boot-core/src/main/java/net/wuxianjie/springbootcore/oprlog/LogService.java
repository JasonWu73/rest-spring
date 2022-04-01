package net.wuxianjie.springbootcore.oprlog;

/**
 * 操作日志持久化保存接口。
 *
 * @author 吴仙杰
 */
public interface LogService {

    /**
     * 保存操作日志数据。
     *
     * @param logData 日志数据
     */
    void saveLog(LogData logData);
}
