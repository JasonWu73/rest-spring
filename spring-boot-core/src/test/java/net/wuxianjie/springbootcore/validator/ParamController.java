package net.wuxianjie.springbootcore.validator;

import lombok.Data;
import net.wuxianjie.springbootcore.mybatis.YesOrNo;
import net.wuxianjie.springbootcore.validator.group.Save;
import net.wuxianjie.springbootcore.validator.group.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 吴仙杰
 */
@Validated
@RestController
@RequestMapping("/param")
class ParamController {

    @GetMapping("enum")
    void test(
            @EnumValidator(message = "状态值错误", value = YesOrNo.class)
                    Integer enabled
    ) {
    }

    @GetMapping("enum-2")
    void test2(
            @EnumValidator(message = "类型值错误", value = TypeNoValueMethod.class)
                    String type
    ) {
    }

    @GetMapping("enum-3")
    void test3(
            @EnumValidator(message = "类型值错误", value = TypeErrorValueMethod.class)
                    String type
    ) {
    }

    @GetMapping("save")
    void save(@Validated(Save.class) Param param) {
    }

    @GetMapping("update")
    void update(@Validated(Update.class) Param param) {
    }

    @Data
    static class Param {

        @NotNull(message = "启用状态不能为 null", groups = Save.class)
        private Integer enabled;

        @NotNull(message = "ID 不能为 null", groups = Update.class)
        private Integer id;

        @NotBlank(message = "名称不能为空")
        private String name;
    }
}
