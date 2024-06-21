package com.example.search.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class KeywordSearchRequest {
    @Schema(description = "검색어", example = "keyword")
    private String keyword;

    public List<String> getKeywords() {
        return Stream.of(keyword.split(" ")).map(String::toLowerCase).toList();
    }
}
