package net.wuxianjie.springbootcore.log;

import org.springframework.stereotype.Service;

/**
 * @author 吴仙杰
 */
@Service
class ApiService {

    @Logger("调用无参无返回值方法")
    void callMethod() {
    }

    @Logger("调用有原始类型入参及返回 null 值方法")
    Integer callMethodReturnNull(int i) {
        return null;
    }

    @Logger("调用有原始类型入参及返回值方法")
    int callMethod(int i) {
        return i;
    }
}
