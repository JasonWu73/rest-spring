package net.wuxianjie.web.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.dto.PaginationDto;
import net.wuxianjie.core.dto.PaginationQueryDto;
import net.wuxianjie.core.dto.PrincipalDto;
import net.wuxianjie.core.service.AuthenticationFacade;
import net.wuxianjie.web.dto.OperationLogDto;
import net.wuxianjie.web.mapper.OperationLogMapper;
import net.wuxianjie.web.model.OperationLog;
import net.wuxianjie.web.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper logMapper;
    private final AuthenticationFacade authentication;

    @Override
    public PaginationDto<List<OperationLogDto>> getOperationLogs(
            @NonNull final PaginationQueryDto pagination,
            @NonNull final LocalDateTime startTime,
            @NonNull final LocalDateTime endTime
    ) {
        // 获取分页数据
        final List<OperationLog> logs = logMapper.findByPagination(pagination, startTime, endTime);
        final int total = logMapper.countByStartEndTime(startTime, endTime);

        // 构造 DTO
        final List<OperationLogDto> logList = logs.stream()
                .map(OperationLogDto::new)
                .collect(Collectors.toList());

        // 返回分页结果
        return new PaginationDto<>(total, pagination.getPageNo(), pagination.getPageSize(), logList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOperationLog(
            @NonNull final LocalDateTime operationTime,
            @NonNull final String message
    ) {
        // 获取当前用户信息
        final PrincipalDto principal = authentication.getPrincipal();
        final Integer userId = principal.getAccountId();
        final String username = principal.getAccountName();

        // 生成日志数据
        final OperationLog logToAdd = new OperationLog(null, operationTime, userId, username, message);

        // 入库
        logMapper.add(logToAdd);
    }
}
