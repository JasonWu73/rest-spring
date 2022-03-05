package net.wuxianjie.web.mapper;

import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.web.model.OperationLog;
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
