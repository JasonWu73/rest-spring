package net.wuxianjie.web.operationlog;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.wuxianjie.core.rest.auth.AuthenticationFacade;
import net.wuxianjie.core.rest.auth.dto.PrincipalDto;
import net.wuxianjie.core.shared.pagination.PaginationDto;
import net.wuxianjie.core.shared.pagination.PaginationQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OperationLogService {

    private final OperationLogMapper logMapper;
    private final AuthenticationFacade authentication;

    public PaginationDto<List<OperationLogDto>> getOperationLogs(
            @NonNull final PaginationQueryDto pagination,
            @NonNull final LocalDateTime startTime,
            @NonNull final LocalDateTime endTime
    ) {
        // 获取分页数据
        final List<OperationLog> logs = logMapper
                .findByPagination(pagination, startTime, endTime);
        final int total = logMapper.countByStartEndTime(startTime, endTime);

        // 构造 DTO
        final List<OperationLogDto> logList = logs.stream()
                .map(OperationLogDto::new)
                .collect(Collectors.toList());

        // 返回分页结果
        return new PaginationDto<>(
                total,
                pagination.getPageNo(),
                pagination.getPageSize(),
                logList
        );
    }

    /**
     * 新增操作日志
     *
     * @param operationTime 操作的时间
     * @param message       需指明具体操作内容，如新增或删除了什么、将什么修改为什么
     */
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
        final OperationLog logToAdd = new OperationLog(
                null,
                operationTime,
                userId,
                username,
                message
        );

        // 入库
        logMapper.add(logToAdd);
    }
}
