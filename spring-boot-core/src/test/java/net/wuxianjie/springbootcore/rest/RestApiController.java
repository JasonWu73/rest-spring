package net.wuxianjie.springbootcore.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 吴仙杰
 */
@RestController
class RestApiController {

    @GetMapping(value = "/hello", produces = MediaType.TEXT_HTML_VALUE)
    String getImage() {
        return "<h1>Hello World</h1>";
    }
}
