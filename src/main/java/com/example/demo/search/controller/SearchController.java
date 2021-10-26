package com.example.demo.search.controller;

import com.example.demo.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/search/{keyword}")
    public void search(@PathVariable String keyword) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        searchService.searchKeyword(keyword);
        stopWatch.stop();
        System.out.println("검색 수행시간 : " + stopWatch.getTotalTimeMillis() + "ms");
    }
}
