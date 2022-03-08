package net.wuxianjie.core.paging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

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
        Object[] args = joinpoint.getArgs();

        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof PagingQuery) {
                    PagingQuery paging = (PagingQuery) arg;

                    paging.setOffset();
                }
            }
        }
    }
}
