package net.wuxianjie.springbootcore.validator;

import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.validator.group.Save;
import net.wuxianjie.springbootcore.validator.group.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 吴仙杰
 */
@RestController
@RequestMapping("/param")
class ParamController {

    @GetMapping("/save")
    void save(@Validated(Save.class) Param param) {
    }

    @GetMapping("/update")
    void update(@Validated(Update.class) Param param) {
    }

    @GetMapping("/wrong")
    void wrong(@Valid WrongParam param) {
    }

    @GetMapping("/wrong-2")
    void wrong2(@Valid WrongParam2 param) {
    }

    @Data
    static class WrongParam2 {

        @EnumValidator(message = "HTTP 状态码错误", value = Type2.class)
        private String type;
    }

    @Data
    static class WrongParam {

        @EnumValidator(message = "HTTP 状态码错误", value = Type.class)
        private String type;
    }

    @Data
    static class Param {

        @NotNull(message = "启用状态不能为 null", groups = Save.class)
        @EnumValidator(message = "启用状态错误", value = YesOrNo.class)
        private Integer enabled;

        @NotNull(message = "ID 不能为 null", groups = Update.class)
        private Integer id;
    }
}
