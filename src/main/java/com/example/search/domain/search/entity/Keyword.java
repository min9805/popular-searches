package com.example.search.domain.search.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Keyword {
    private String keyword;
    private double score;
}