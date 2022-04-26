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

  @PostMapping("/test")
  @OperationLogger("测试 API")
  OuterResult getResult(@RequestBody OuterParameter param) {
    return new OuterResult() {{
      setError(0);
      setMessage("成功");
      setData(new InnerResult() {{
        setList(List.of("One", "Two"));
        setSize(2);
      }});
    }};
  }

  @OperationLogger("入参与返回值都是 int 的方法")
  int callMethod(int i) {
    return i;
  }

  @OperationLogger("入参为 int，返回值为 null 值的方法")
  Integer callMethodReturnNull(int i) {
    return null;
  }

  @OperationLogger("无参无返回值的方法")
  void callMethod() {
  }

  @Data
  static class OuterParameter {

    private String name;
    private InnerParameter data;
  }

  @Data
  static class InnerParameter {

    private String name;
    private Integer status;
  }

  @Data
  static class OuterResult {

    private Integer error;
    private String message;
    private InnerResult data;
  }

  @Data
  static class InnerResult {

    private List<String> list;
    private Integer size;
  }
}
