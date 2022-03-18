package net.wuxianjie.web.operationlog;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingData;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.security.Admin;
import net.wuxianjie.springbootcore.shared.BadRequestException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 操作日志。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService logService;

    /**
     * 获取操作日志列表。
     */
    @Admin
    @GetMapping("list")
    public PagingData<List<ListOfOperationLogItem>> getOperationLogs(@Validated PagingQuery paging,
                                                                     @Validated GetOperationLogQuery query) {
        LocalDateTime startTime = getStartTimeOfDay(query.getStartDate());
        query.setStartTimeInclusive(startTime);

        LocalDateTime endTime = getEndTimeOfDay(query.getEndDate());
        query.setEndTimeInclusive(endTime);

        return logService.getOperationLogs(paging, query);
    }

    private LocalDateTime getEndTimeOfDay(String dateStr) {
        if (StrUtil.isEmpty(dateStr)) {
            return null;
        }

        LocalDate endDate;
        try {
            endDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("结束日期错误", e);
        }
        return endDate.atTime(LocalTime.MAX);
    }

    private LocalDateTime getStartTimeOfDay(String dateStr) {
        if (StrUtil.isEmpty(dateStr)) {
            return null;
        }

        LocalDate startDate;
        try {
            startDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("开始日期不合法", e);
        }
        return startDate.atStartOfDay();
    }
}
