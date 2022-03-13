package net.wuxianjie.web.operationlog;

import net.wuxianjie.core.paging.PagingQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {

  @NonNull
  List<OperationLog> findByStartEndTimePagingOperationTimeDesc(@Param("p") PagingQuery paging,
                                                               @Param("start") LocalDateTime startInclusive,
                                                               @Param("end") LocalDateTime endInclusive
  );

  int countByStartEndTime(@Param("start") LocalDateTime startInclusive,
                          @Param("end") LocalDateTime endInclusive
  );

  int add(OperationLog log);
}
