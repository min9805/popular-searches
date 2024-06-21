package com.example.search.domain.search.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Getter
@Document(indexName = "ranking")
@AllArgsConstructor
@Builder
public class KeyWordRank {
    @Id
    private String id;

    private Long startDate;
    private List<Keyword> keyWords;


    public List<Keyword> getTopThreeKeywords() {
        if (keyWords.size() <= 3) {
            return keyWords;
        } else {
            return keyWords.subList(0, 3);
        }
    }
}

