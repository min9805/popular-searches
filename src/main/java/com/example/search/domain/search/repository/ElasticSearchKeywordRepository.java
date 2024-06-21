package com.example.search.domain.search.repository;

import com.example.search.domain.search.entity.SearchKeyword;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticSearchKeywordRepository extends ElasticsearchRepository<SearchKeyword, String> {
}
