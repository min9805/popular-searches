package com.example.search.domain.search.controller;

import com.example.search.domain.search.dto.KeywordSearchRequest;
import com.example.search.domain.search.dto.SearchRankResponse;
import com.example.search.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/searchRank")
@RequiredArgsConstructor
public class SearchRankController {

    private final SearchService searchRankService;

    @GetMapping("/")
    public List<SearchRankResponse> searchRank() {
        return searchRankService.SearchRankList();
    }

    @PostMapping("/search")
    public String search(KeywordSearchRequest keywordSearchRequest) {
        searchRankService.keywordSearch(keywordSearchRequest);
        return "search";
    }
}
