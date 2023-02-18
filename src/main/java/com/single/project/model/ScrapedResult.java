package com.single.project.model;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScrapedResult {
    private Company company;
    private List<Dividend> dividends;
}
