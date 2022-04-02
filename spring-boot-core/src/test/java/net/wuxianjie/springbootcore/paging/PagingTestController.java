package net.wuxianjie.springbootcore.paging;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 吴仙杰
 */
@RestController
class PagingTestController {

    @GetMapping("/paging")
    public PagingResult<String> getPagingList(final @Valid PagingQuery paging) {
        return buildPagingResult(paging);
    }

    private PagingResult<String> buildPagingResult(final PagingQuery paging) {
        final List<String> allData = List.of("One", "Two", "Three", "Four", "Five");
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
