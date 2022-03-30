package net.wuxianjie.springbootcore.log;

/**
 * 操作日志持久化保存接口。
 *
 * @author 吴仙杰
 */
public interface OperationService {

    void saveLog(OperationLog log);
}
