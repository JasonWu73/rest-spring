package net.wuxianjie.springbootcore.paging;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 吴仙杰
 */
@RestController
class PagingSearchController {

    @GetMapping("/paging")
    public PagingResult<String> getPagingList(PagingQuery paging) {
        return buildPagingResult(paging);
    }

    private PagingResult<String> buildPagingResult(PagingQuery paging) {
        List<String> allData = List.of(
                "One",
                "Two",
                "Three",
                "Four",
                "Five"
        );

        return new PagingResult<>(
                paging,
                allData.size(),
                allData.stream()
                        .skip(paging.getOffset())
                        .limit(paging.getPageSize())
                        .collect(Collectors.toList())
        );
    }
}
