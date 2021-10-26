package com.example.demo.search.controller;

import com.example.demo.index.model.DocInfo;
import com.example.demo.index.runner.IndexRunner;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SearchController {
    private final IndexRunner indexRunner;

    @GetMapping("/search/{keyword}")
    public void search(@PathVariable String keyword) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        System.out.println("검색어 > " + keyword);
        keyword = keyword.toLowerCase();
        String[] tokens = keyword.split(" ");
        Map<String, Double> result = new HashMap<>();
        for (String token : tokens) {
            token = token.replaceAll("[^a-z0-9]","").trim();
            if (indexRunner.getIndex().containsKey(token)) {
                List<DocInfo> docInfoList = indexRunner.getIndex().get(token);
                for (DocInfo docInfo : docInfoList) {
                    if (result.containsKey(docInfo.getFileName())) {
                        result.put(docInfo.getFileName(), result.get(docInfo.getFileName()) + docInfo.getScore());
                    } else {
                        result.put(docInfo.getFileName(), docInfo.getScore());
                    }
                }
            }
        }
        AtomicInteger rank = new AtomicInteger(1);
        result.entrySet().stream()
            .filter(d -> !ObjectUtils.isEmpty(d.getValue()))
            .sorted(Entry.comparingByValue(Comparator.reverseOrder()))
            .forEach(d -> System.out.println("Rank [" + rank.getAndIncrement() + "] " + d.getKey() + " > score : " + Math.round(d.getValue() * 10000) / 10000.0 ));
        stopWatch.stop();
        System.out.println("검색 수행시간 : " + stopWatch.getTotalTimeMillis() + "ms");
    }
}
