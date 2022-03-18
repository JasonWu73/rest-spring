package net.wuxianjie.springbootcore.paging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 自动识别 Controller 类中，需要分页查询的方法，并自动填充分页查询参数中的 OFFSET 字段值。
 *
 * @author 吴仙杰
 * @see PagingQuery
 */
@Aspect
@Component
public class PagingParameterPaddingAop {

    // execution([方法的可见性] 返回类型 [方法所在类的全路径名] 方法名(参数类型列表) [方法抛出的异常类型])
    @Pointcut("execution(public net.wuxianjie.springbootcore.paging.PagingData *..*Controller.*(net.wuxianjie.springbootcore.paging.PagingQuery, ..))")
    public void getByPaging() {
    }

    // 匹配任何包下以 Controller 结尾的类，且第一个参数为 PagingQuery，且返回值为 PagingData 的方法
    // 匹配所有符合以下条件的方法：
    // 1. 类名后缀为 Controller
    // 2. 方法的第一个参数为 net.wuxianjie.springbootcore.paging.PagingQuery
    // 3. 方法的返回值为 net.wuxianjie.springbootcore.paging.PagingData
    @Before("getByPaging()")
    public void beforeCallGetByPagingMethod(JoinPoint joinpoint) {
        Optional.ofNullable(joinpoint.getArgs())
                .ifPresent(args -> {
                    for (Object arg : args) {
                        if (arg instanceof PagingQuery) {
                            PagingQuery pagingQueryArg = (PagingQuery) arg;
                            pagingQueryArg.setOffset();
                        }
                    }
                });
    }
}
