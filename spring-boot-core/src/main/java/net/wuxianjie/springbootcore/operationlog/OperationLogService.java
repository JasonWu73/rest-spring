package net.wuxianjie.springbootcore.operationlog;

/**
 * 操作日志持久化保存接口。
 * <p>
 * 将 {@link OperationLogger} 用于方法后，只要该方法正确执行后，会自动调用 {@link OperationLogService#saveLog(OperationLogData)} 进行处理。
 * </p>
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
