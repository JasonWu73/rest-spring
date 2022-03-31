package net.wuxianjie.springbootcore.log;

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

    @Logger("测试方法")
    @PostMapping("/test")
    Result getResult(@RequestBody Param param) {
        return new Result() {{
            setError(0);
            setMessage("成功");
            setData(new ResultData() {{
                setList(List.of("One", "Two"));
                setSize(2);
            }});
        }};
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
