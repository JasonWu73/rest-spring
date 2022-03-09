package net.wuxianjie.core.paging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 自动识别拥有分页查询参数的 Controller 方法，并自动调用填充 OFFSET 参数。
 *
 * @see PagingQuery
 */
@Aspect
@Component
public class PagingParamPaddingAspect {

    @Pointcut("execution(" +
            "public net.wuxianjie.core.paging.PagingData " +
            "net.wuxianjie.*.*.*.*" +
            "(net.wuxianjie.core.paging.PagingQuery, ..))"
    )
    public void getByPaging() {
    }

    @Before("getByPaging()")
    public void beforeCallGetByPagingMethod(JoinPoint joinpoint) {
        final Object[] args = joinpoint.getArgs();

        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof PagingQuery) {
                    final PagingQuery paging = (PagingQuery) arg;

                    paging.setOffset();
                }
            }
        }
    }
}
