package com.example.search.domain.search.service;

import com.example.search.domain.search.dto.KeywordSearchRequest;
import com.example.search.domain.search.dto.SearchRankResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchService {
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void keywordSearch(KeywordSearchRequest keywordSearchRequest) {
        try {
            redisTemplate.opsForZSet().incrementScore("ranking", keywordSearchRequest.getKeyword(), 1);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    // 인기검색어 리스트 1위~10위까지
    public List<SearchRankResponse> SearchRankList() {
        String key = "ranking";
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 9);
        assert typedTuples != null;
        return typedTuples.stream().map(SearchRankResponse::convertToResponseRankingDto).collect(Collectors.toList());
    }
}