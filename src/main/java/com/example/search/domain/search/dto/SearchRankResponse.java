package com.example.search.domain.search.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.ZSetOperations;

@Getter
@AllArgsConstructor
public class SearchRankResponse {
    @Schema(description = "검색어", example = "keyword")
    private String keyword;

    @Schema(description = "점수", example = "1.0")
    private Double score;

    public static SearchRankResponse convertToResponseRankingDto(ZSetOperations.TypedTuple<String> stringTypedTuple) {
        return new SearchRankResponse(stringTypedTuple.getValue(), stringTypedTuple.getScore());
    }
}
