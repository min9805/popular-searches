package com.example.search.domain.search.controller;

import com.example.search.domain.search.dto.KeywordSearchRequest;
import com.example.search.domain.search.dto.SearchRankResponseDto;
import com.example.search.domain.search.service.SearchRankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/searchRank")
@RequiredArgsConstructor
@Slf4j
public class SearchRankController {

    private final SearchRankService searchRankService;

    @PostMapping("/search")
    @Operation(summary = "검색어 등록", description = "실시간 검색어를 등록합니다.", tags = {"Search"})
    @ApiResponse(
            responseCode = "200",
            description = "검색어 등록 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SearchRankResponseDto.class)
            )
    )
    public List<SearchRankResponseDto> search(KeywordSearchRequest keywordSearchRequest) {
        log.info("keywordSearchRequest: {}", keywordSearchRequest.getKeyword());
        return searchRankService.keywordSearch(keywordSearchRequest);
    }

    @GetMapping("/rank")
    @Operation(summary = "검색어 순위 Top 10 조회", description = "실시간 검색어 순위 Top 10 을 조회합니다.", tags = {"Search"})
    @ApiResponse(
            responseCode = "200",
            description = "검색어 순위 조회 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)
            )
    )
    public List<SearchRankResponseDto> searchRank() {
        return searchRankService.SearchRankList();
    }
    
}
