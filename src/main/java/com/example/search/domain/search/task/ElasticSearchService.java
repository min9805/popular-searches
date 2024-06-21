package com.example.search.domain.search.task;

import com.example.search.domain.search.entity.KeyWordRank;
import com.example.search.domain.search.entity.Keyword;
import com.example.search.domain.search.entity.SearchKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticSearchService {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final RedisTemplate<String, String> redisTemplate;
    private final ElasticsearchOperations elasticsearchOperations;

    @Scheduled(fixedRate = 600000) // 10 minutes
    public void migrateDataRedisToElastic() {
        log.info("migrateDataRedisToElastic");

        PriorityQueue<Keyword> maxHeap = new PriorityQueue<>(new Comparator<Keyword>() {
            @Override
            public int compare(Keyword k1, Keyword k2) {
                return Double.compare(k2.getScore(), k1.getScore());
            }
        });

        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> ranking = zSetOps.rangeWithScores("ranking", 0, 19);

        if (ranking != null) {
            long currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

            for (ZSetOperations.TypedTuple<String> tuple : ranking) {
                String keyword = tuple.getValue();
                Double score = tuple.getScore();
                int tenMinCount = score != null ? score.intValue() : 0;

                NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                        .withQuery(QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery("keyword", keyword))
                                .must(QueryBuilders.rangeQuery("startDate").gte(LocalDateTime.now().minusHours(1).toEpochSecond(ZoneOffset.UTC))))
                        .addAggregation(AggregationBuilders.sum("totalTenMinCount").field("tenMinCount"))
                        .build();
                Aggregations aggregations = elasticsearchOperations.search(searchQuery, SearchKeyword.class).getAggregations();
                Sum totalTenMinCount = aggregations.get("totalTenMinCount");
                System.out.println(totalTenMinCount.getValue());
                int totalTenMinCountValue = (int) totalTenMinCount.getValue();

                // Create SearchKeyword object
                SearchKeyword searchKeyword = SearchKeyword.builder()
                        .startDate(currentTime)
                        .keyword(keyword)
                        .tenMinCount(tenMinCount)
                        .sixtyMinCount(totalTenMinCountValue)
                        .build();

                // Save to Elasticsearch
                elasticsearchOperations.save(searchKeyword);

                double keywordScore = (tenMinCount * 6.0 / totalTenMinCountValue) * tenMinCount;

                // Add to maxHeap
                maxHeap.add(new Keyword(keyword, keywordScore));
            }

            // Clear all Redis data
            redisTemplate.delete("ranking");

            List<Keyword> resultKeywords = new ArrayList<>();
            int count = 0;
            while (!maxHeap.isEmpty() && count < 10) {
                Keyword keyword = maxHeap.poll();
                resultKeywords.add(keyword);
                count++;
            }

            KeyWordRank keyWordRank = KeyWordRank.builder()
                    .startDate(currentTime)
                    .keyWords(resultKeywords)
                    .build();

            // Save to Elasticsearch
            elasticsearchOperations.save(keyWordRank);
        }
    }

}
