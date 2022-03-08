package net.wuxianjie.web.operationlog;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.core.paging.PagingData;
import net.wuxianjie.core.paging.PagingQuery;
import net.wuxianjie.core.security.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 操作日志
 */
@Validated
@RestController
@RequestMapping("/api/v1/operation-log")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogController {

    private final OperationLogService logService;

    /**
     * 获取操作日志列表
     */
    @Admin
    @GetMapping("list")
    public PagingData<List<ListItemOfOperationLog>> getOperationLogs(
            @Validated PagingQuery paging,

            @NotNull(message = "开始日期不能为空")
            @Pattern(message = "开始日期不符合 yyyy-MM-dd 格式",
                    regexp = "^\\d{4}-\\d{2}-\\d{2}$"
            ) String startDate,

            @NotNull(message = "结束日期不能为空")
            @Pattern(message = "结束日期不符合 yyyy-MM-dd 格式",
                    regexp = "^\\d{4}-\\d{2}-\\d{2}$"
            ) String endDate
    ) {
        LocalDate startDateInclusive = LocalDate.parse(startDate);
        LocalDate endDateInclusive = LocalDate.parse(endDate);

        if (startDateInclusive.isAfter(endDateInclusive)) {
            throw new BadRequestException("开始日期不能晚于结束日期");
        }

        LocalDateTime startTime = startDateInclusive.atStartOfDay();
        LocalDateTime endTime = endDateInclusive.atTime(LocalTime.MAX);

        return logService.getOperationLogs(paging, startTime, endTime);
    }
}
