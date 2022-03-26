package net.wuxianjie.springbootcore.paging;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author 吴仙杰
 */
@RestController
class PagingSearchController {

    @GetMapping("/paging")
    public PagingResult<String> getPagingList(@Valid PagingQuery paging) {
        return buildPagingResult(paging);
    }

    private PagingResult<String> buildPagingResult(PagingQuery paging) {
        ArrayList<String> allList = new ArrayList<>() {{
            add("One");
            add("Two");
            add("Three");
            add("Four");
            add("Five");
        }};

        return new PagingResult<>(paging, allList.size(), allList.stream()
                .skip(paging.getOffset())
                .limit(paging.getPageSize())
                .collect(Collectors.toList()));
    }
}
