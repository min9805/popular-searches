package com.example.search.domain.search.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Document(indexName = "keywords")
@AllArgsConstructor
@Builder
public class SearchKeyword {
    private String id;
    private Long startDate;
    private String keyword;
    private int tenMinCount;
    private int sixtyMinCount;
}
