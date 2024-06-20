package com.example.search.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordSearchRequest {
    @Schema(description = "검색어", example = "keyword")
    private String keyword;
}
