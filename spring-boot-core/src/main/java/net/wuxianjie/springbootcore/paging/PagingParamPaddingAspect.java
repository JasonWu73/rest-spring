package net.wuxianjie.springbootcore.paging;

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

  // execution(
  // [方法的可见性] 返回类型
  // [方法所在类的全路径名]
  // 方法名(参数类型列表) [方法抛出的异常类型])
  @Pointcut("execution("
      + "public net.wuxianjie.springbootcore.paging.PagingData "
      + "*..*Controller."
      + "*(net.wuxianjie.springbootcore.paging.PagingQuery, ..))")
  public void getByPaging() {}

  // 匹配任何包下所有以 Controller 结尾的类中第一个参数为 PagingQuery，
  // 返回值为 PagingData 的方法
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