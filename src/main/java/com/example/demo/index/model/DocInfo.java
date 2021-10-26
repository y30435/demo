package com.example.demo.index.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DocInfo {
    private String fileName;
    private Double termCnt;
    private Double totalTermCnt;
    private Double score;
}
