package net.wuxianjie.springbootcore.operationlog;

import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 吴仙杰
 */
@RestController
class ApiTestController {

    @OperationLogger("测试方法")
    @PostMapping("/test")
    Result getResult(@RequestBody final Param param) {
        return new Result() {{
            setError(0);
            setMessage("成功");
            setData(new ResultData() {{
                setList(List.of("One", "Two"));
                setSize(2);
            }});
        }};
    }

    @OperationLogger("调用无参无返回值方法")
    void callMethod() {
    }

    @SuppressWarnings({"unused", "SameParameterValue"})
    @OperationLogger("调用有原始类型入参及返回 null 值方法")
    Integer callMethodReturnNull(final int i) {
        return null;
    }

    @SuppressWarnings({"unused", "SameParameterValue"})
    @OperationLogger("调用有原始类型入参及返回原始类型值方法")
    int callMethod(final int i) {
        return i;
    }

    @Data
    static class Param {

        private String name;
        private ParamData data;
    }

    @Data
    static class ParamData {

        private String name;
        private Integer status;
    }

    @Data
    static class Result {

        private Integer error;
        private String message;
        private ResultData data;
    }

    @Data
    static class ResultData {

        private List<String> list;
        private Integer size;
    }
}
