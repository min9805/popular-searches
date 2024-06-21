package com.example.search.domain.search.service;

import com.example.search.domain.search.dto.KeywordSearchRequest;
import com.example.search.domain.search.dto.SearchRankResponseDto;
import com.example.search.domain.search.entity.KeyWordRank;
import com.example.search.domain.search.entity.Keyword;
import com.example.search.domain.search.repository.ElasticSearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SearchRankService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ElasticSearchKeywordRepository elasticSearchKeywordRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<SearchRankResponseDto> keywordSearch(KeywordSearchRequest keywordSearchRequest) {
        try {
            return keywordSearchRequest.getKeywords().stream().map(keyword -> {
                Double score = redisTemplate.opsForZSet().incrementScore("ranking", keyword, 1);
                return new SearchRankResponseDto(keyword, score);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

//    // 인기검색어 리스트 1위~10위까지
//    public List<SearchRankResponseDto> SearchRankList() {
//        String key = "ranking";
//        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
//        Set<ZSetOperations.TypedTuple<String>> typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 9);
//        assert typedTuples != null;
//        return typedTuples.stream().map(SearchRankResponseDto::convertToResponseRankingDto).collect(Collectors.toList());
//    }

    public List<SearchRankResponseDto> SearchRankList() {
        SortBuilder<FieldSortBuilder> sortBuilder = SortBuilders.fieldSort("startDate").order(SortOrder.DESC);


        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(sortBuilder)
                .withMaxResults(1)
                .build();

        SearchHits<KeyWordRank> search = elasticsearchOperations.search(searchQuery, KeyWordRank.class);

        KeyWordRank keyWordRank = elasticsearchOperations.search(searchQuery, KeyWordRank.class).getSearchHits().stream()
                .findFirst()
                .map(searchHit -> searchHit.getContent())
                .orElse(null);

        if (keyWordRank == null) {
            return null;
        }

        List<Keyword> topThreeKeywords = keyWordRank.getTopThreeKeywords();

        return topThreeKeywords.stream()
                .map(keyword -> new SearchRankResponseDto(keyword.getKeyword(), keyword.getScore()))
                .collect(Collectors.toList());
    }
}