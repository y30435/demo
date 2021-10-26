package com.example.demo.index.service;

import com.example.demo.index.model.DocInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class IndexService {
    private Map<String,List<DocInfo>> index = new HashMap<>();

    public Map<String, List<DocInfo>> index(String path) throws IOException {
        int fileCnt = 0;
        List<Path> fileList = Files.walk(Paths.get(path)).collect(Collectors.toList());
        for (Path file : fileList) {
            try {
                if (Files.exists(file) && !String.valueOf(file).replace(path,"").equals("")) {
                    fileCnt++;
                    makeIndexData(file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(String key : index.keySet()) {
            List<DocInfo> docInfoList = index.get(key);
            for (DocInfo docInfo : docInfoList) {
                docInfo.setScore(Math.round((( docInfo.getTermCnt() / docInfo.getTotalTermCnt() ) * (1 + Math.log( fileCnt / docInfoList.size() ))) * 10000) / 10000.0 + 1);
            }
        }
        return index;
    }

    private void makeIndexData(Path file) throws IOException {
        String fileName = file.getFileName().toString();
        List<String> lines = Files.readAllLines(file);
        Set<String> fileTerm = new HashSet<>();
        for (String line : lines) {
            tokenToIndex(fileName, fileTerm, line);
        }
        for(String key : index.keySet()) {
            List<DocInfo> docInfoList = index.get(key);
            for (DocInfo docInfo : docInfoList) {
                if (docInfo.getFileName().equalsIgnoreCase(fileName)) {
                    docInfo.setTotalTermCnt(Double.valueOf(fileTerm.size()));
                }
            }
        }
    }

    private void tokenToIndex(String fileName, Set<String> fileTerm, String line) {
        String[] tokens = line.split(" ");
        for (String token : tokens) {
            boolean done = false;
            token = token.toLowerCase();
            token = token.replaceAll("[^a-z0-9]","").trim();
            if (ObjectUtils.isEmpty(token)) {
                continue;
            }
            fileTerm.add(token);
            if (index.containsKey(token)) {
                List<DocInfo> docInfoList = index.get(token);
                for (DocInfo docInfo : docInfoList) {
                    if (fileName.equals(docInfo.getFileName())) {
                        docInfo.setTermCnt(docInfo.getTermCnt() + 1);
                        done = true;
                    }
                }
                if (!done) {
                    putIndex(fileName, token, docInfoList);
                }
            } else {
                List<DocInfo> docInfoList = new ArrayList<>();
                putIndex(fileName, token, docInfoList);
            }
        }
    }

    private void putIndex(String fileName, String token, List<DocInfo> docInfoList) {
        DocInfo docInfo = new DocInfo();
        docInfo.setFileName(fileName);
        docInfo.setTermCnt(1D);
        docInfoList.add(docInfo);
        index.put(token, docInfoList);
    }
}
