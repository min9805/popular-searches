package com.example.search.domain.search.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.redis.core.ZSetOperations;

@Getter
@AllArgsConstructor
public class SearchRankResponse {
    private String keyword;
    private Double score;

    public static SearchRankResponse convertToResponseRankingDto(ZSetOperations.TypedTuple<String> stringTypedTuple) {
        return new SearchRankResponse(stringTypedTuple.getValue(), stringTypedTuple.getScore());
    }
}
