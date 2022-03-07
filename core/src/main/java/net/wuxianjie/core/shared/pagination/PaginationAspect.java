package net.wuxianjie.core.shared.pagination;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 自动填充分页参数的偏移量
 */
@Aspect
@Component
public class PaginationAspect {

    @Pointcut("execution(" +
            "public net.wuxianjie.core.shared.pagination.PaginationDto " +
            "net.wuxianjie.*.*.*.*" +
            "(net.wuxianjie.core.shared.pagination.PaginationQueryDto, ..))"
    )
    public void paginationMethod() {
    }

    @Before("paginationMethod()")
    public void beforeGetPaginationData(final JoinPoint joinpoint) {
        final Object[] args = joinpoint.getArgs();

        if (args != null) {
            for (final Object arg : args) {
                if (arg instanceof PaginationQueryDto) {
                    final PaginationQueryDto paginationQuery =
                            (PaginationQueryDto) arg;

                    paginationQuery.setOffset();
                }
            }
        }
    }
}
