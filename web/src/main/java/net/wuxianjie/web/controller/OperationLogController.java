package net.wuxianjie.web.controller;

import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.annotation.Admin;
import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.core.exception.BadRequestException;
import net.wuxianjie.web.model.OperationLog;
import net.wuxianjie.web.service.OperationLogService;
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
@RequestMapping("/operation-log")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogController {

    private final OperationLogService logService;

    /**
     * 获取操作日志分页数据
     *
     * @param pagination 分页条件
     * @param startDate  开始日期（包含），必填，格式为 yyyy-MM-dd，例如：2022-02-10
     * @param endDate    结束日期（包含），必填，格式为 yyyy-MM-dd，例如：2022-02-12
     * @return 操作日志分页数据
     */
    @Admin
    @GetMapping("list")
    public PaginationDto<List<OperationLog>> getOperationLogs(
            @Validated final PaginationQueryDto pagination,
            @NotNull(message = "开始日期不能为空")
            @Pattern(message = "开始日期格式错误", regexp = "^\\d{4}-\\d{2}-\\d{2}$") final String startDate,
            @NotNull(message = "结束日期不能为空")
            @Pattern(message = "结束日期格式错误", regexp = "^\\d{4}-\\d{2}-\\d{2}$") final String endDate
    ) {
        // 解析日期字符串
        final LocalDate startLocalDate = LocalDate.parse(startDate);
        final LocalDate endLocalDate = LocalDate.parse(endDate);

        // 判断开始和结束日期是否合法
        if (startLocalDate.isAfter(endLocalDate)) {
            throw new BadRequestException("开始日期不能晚于结束日期");
        }

        // 分别获取开始日期的一天的开始和结束时间
        final LocalDateTime startTime = startLocalDate.atStartOfDay();
        final LocalDateTime endTime = endLocalDate.atTime(LocalTime.MAX);

        return logService.getOperationLogs(pagination, startTime, endTime);
    }
}
