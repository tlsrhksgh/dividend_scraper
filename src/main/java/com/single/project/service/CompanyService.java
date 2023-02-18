package com.single.project.service;

import com.single.project.domain.company.CompanyEntity;
import com.single.project.domain.company.CompanyRepository;
import com.single.project.domain.dividend.DividendEntity;
import com.single.project.domain.dividend.DividendRepository;
import com.single.project.model.Company;
import com.single.project.model.ScrapedResult;
import com.single.project.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CompanyService {

    private final YahooFinanceScraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = companyRepository.existsByTicker(ticker);
        if(exists) {
            throw new RuntimeException("이미 있는 ticker 입니다." + ticker);
        }

        return storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("회사 정보가 존재하지 않습니다. ticker : " + ticker);
        }

        ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

        CompanyEntity companyEntity = companyRepository.save(CompanyEntity.builder()
                .name(company.getName())
                .ticker(company.getTicker())
                .build());
        List<DividendEntity> dividendEntityList = scrapedResult.getDividends()
                .stream().map(e -> new DividendEntity(companyEntity.getId(), e))
                .collect(Collectors.toList());

        dividendRepository.saveAll(dividendEntityList);
        return company;
    }

}
