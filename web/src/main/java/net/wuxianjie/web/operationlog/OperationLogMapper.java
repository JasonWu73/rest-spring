package net.wuxianjie.web.operationlog;

import net.wuxianjie.core.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

    List<OperationLog> findByStartEndTimeLimitTimeDesc(
            @Param("p") PagingQuery paging,
            @Param("start") LocalDateTime startTimeInclusive,
            @Param("end") LocalDateTime endTimeInclusive
    );

    int countByStartEndTime(
            @Param("start") LocalDateTime startTimeInclusive,
            @Param("end") LocalDateTime endTimeInclusive
    );

    int add(OperationLog logToAdd);
}
