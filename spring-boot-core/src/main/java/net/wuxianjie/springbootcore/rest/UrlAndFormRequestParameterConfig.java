package net.wuxianjie.springbootcore.rest;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 配置自动去除通过 URL 及 Form 表单提交的请求参数中字符串值的首尾空格。
 *
 * @author 吴仙杰
 */
@ControllerAdvice
public class UrlAndFormRequestParameterConfig {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor trimmerEditor = new StringTrimmerEditor(false);
        binder.registerCustomEditor(String.class, trimmerEditor);
    }
}
