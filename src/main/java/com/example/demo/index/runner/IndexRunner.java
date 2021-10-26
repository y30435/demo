package com.example.demo.index.runner;

import com.example.demo.index.model.DocInfo;
import com.example.demo.index.service.IndexService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@RequiredArgsConstructor
public class IndexRunner implements CommandLineRunner {
    private final IndexService indexService;
    private Map<String,List<DocInfo>> index = new HashMap<>();

    @Override
    public void run(String... args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        index = indexService.index(args[0]);

        stopWatch.stop();
        System.out.println("색인 수행시간 : " + stopWatch.getTotalTimeMillis() + "ms");
    }

    public Map<String,List<DocInfo>> getIndex() {
        return index;
    }
}
