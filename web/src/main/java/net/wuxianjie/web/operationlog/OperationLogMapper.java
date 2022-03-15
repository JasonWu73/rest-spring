package net.wuxianjie.web.operationlog;

import net.wuxianjie.springbootcore.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

  List<OperationLog> findByStartEndTimePagingOperationTimeDesc(
      @Param("p") PagingQuery paging,
      @Param("start") LocalDateTime startTimeInclusive,
      @Param("end") LocalDateTime endTimeInclusive);

  int countByStartEndTime(@Param("start") LocalDateTime startTimeInclusive,
                          @Param("end") LocalDateTime endTimeInclusive);

  int add(OperationLog log);
}
