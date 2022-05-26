package net.wuxianjie.springbootcore.rest;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * URL 及 Form 表单传参的参数处理配置类。
 *
 * <p>
 * 自动去除通过 URL 及 Form 表单中字符串值的首尾空白字符。
 * </p>
 *
 * @author 吴仙杰
 */
@ControllerAdvice
public class UrlAndFormRequestParamConfig {

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
  }
}
