package net.wuxianjie.web.operationlog;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.springbootcore.paging.PagingQuery;
import net.wuxianjie.springbootcore.paging.PagingResult;
import net.wuxianjie.springbootcore.security.Admin;
import net.wuxianjie.springbootcore.exception.BadRequestException;
import net.wuxianjie.springbootcore.util.StrUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/**
 * 操作日志控制器。
 *
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/api/v1/operation-log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogServiceImpl logService;

    /**
     * 获取操作日志列表。
     *
     * @param paging 分页参数
     * @param query  查询参数
     * @return 日志列表
     */
    @Admin
    @GetMapping("list")
    public PagingResult<OperationLogDto> getLogs(@Validated final PagingQuery paging,
                                                 @Validated final OperationLogQuery query) {
        setFuzzySearchValue(query);

        final LocalDateTime startTime = toStartTimeOfDay(query.getStartDate());
        query.setStartTimeInclusive(startTime);

        final LocalDateTime endTime = toEndTimeOfDay(query.getEndDate());
        query.setEndTimeInclusive(endTime);

        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new BadRequestException("开始日期不能晚于结束日期");
        }

        return logService.getLogs(paging, query);
    }

    private LocalDateTime toEndTimeOfDay(final String dateStr) {
        if (StrUtil.isEmpty(dateStr)) return null;

        final LocalDate endDate;
        try {
            endDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("结束日期不合法", e);
        }

        return endDate.atTime(LocalTime.MAX);
    }

    private LocalDateTime toStartTimeOfDay(final String dateStr) {
        if (StrUtil.isEmpty(dateStr)) return null;

        final LocalDate startDate;
        try {
            startDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("开始日期不合法", e);
        }

        return startDate.atStartOfDay();
    }

    private void setFuzzySearchValue(final OperationLogQuery query) {
        query.setUsername(StrUtils.toFuzzy(query.getUsername()));
        query.setRequestIp(StrUtils.toFuzzy(query.getRequestIp()));
        query.setMethodMessage(StrUtils.toFuzzy(query.getMethodMessage()));
    }
}
