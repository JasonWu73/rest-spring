package net.wuxianjie.web.operationlog;

import net.wuxianjie.springbootcore.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志表相关。
 *
 * @author 吴仙杰
 */
@Mapper
public interface OperationLogMapper {

    List<OperationLogDto> selectLogs(@Param("p") PagingQuery paging,
                                     @Param("q") OperationLogQuery query);

    int countLogs(@Param("q") OperationLogQuery query);

    void insertLog(OperationLog log);
}
