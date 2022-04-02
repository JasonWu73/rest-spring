package net.wuxianjie.springbootcore.paging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * 自动填充分页查询参数中的 offset 参数。
 *
 * @author 吴仙杰
 * @see PagingQuery
 */
@Aspect
@Component
public class PagingOffsetFieldPaddingAspect {

    // execution([方法的可见性] 返回类型
    // [方法所在类的全路径名].方法名(参数类型列表) [方法抛出的异常类型])
    @Pointcut("execution(public net.wuxianjie.springbootcore.paging.PagingResult " +
            "*..*Controller.*(net.wuxianjie.springbootcore.paging.PagingQuery, ..))")
    public void pagingSearchPointcut() {
    }

    /**
     * 匹配所有符合以下条件的方法：
     * <ol>
     *     <li>类名后缀为 Controller</li>
     *     <li>方法的访问修饰符为 public</li>
     *     <li>方法的第一个参数为 {@link PagingQuery}</li>
     *     <li>方法的返回值为 {@link PagingResult}</li>
     * </ol>
     *
     * @param joinpoint {@link JoinPoint}
     */
    @Before("pagingSearchPointcut()")
    public void beforeCallGetByPaging(final JoinPoint joinpoint) {
        Optional.ofNullable(joinpoint.getArgs())
                .ifPresent(args -> Arrays.stream(args)
                        .filter(PagingQuery.class::isInstance)
                        .forEach(arg -> ((PagingQuery) arg).setOffset()));
    }
}
