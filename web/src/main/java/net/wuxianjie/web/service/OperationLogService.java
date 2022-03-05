package net.wuxianjie.web.service;

import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.web.dto.OperationLogDto;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationLogService {

    PaginationDto<List<OperationLogDto>> getOperationLogs(
            PaginationQueryDto pagination,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * 新增操作日志
     *
     * @param operationTime 操作的时间
     * @param message       需指明具体操作内容，如新增或删除了什么、将什么修改为什么
     */
    void saveOperationLog(LocalDateTime operationTime, String message);
}
