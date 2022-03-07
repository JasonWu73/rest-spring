package net.wuxianjie.web.operationlog;

import net.wuxianjie.core.shared.pagination.PaginationQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    List<OperationLog> findByPagination(
            @Param("page") PaginationQueryDto pagination,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    int countByStartEndTime(LocalDateTime startTime, LocalDateTime endTime);

    int add(OperationLog logToAdd);
}
