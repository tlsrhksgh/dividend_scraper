package com.single.project.service;

import com.single.project.domain.company.CompanyEntity;
import com.single.project.domain.company.CompanyRepository;
import com.single.project.domain.dividend.DividendEntity;
import com.single.project.domain.dividend.DividendRepository;
import com.single.project.model.Company;
import com.single.project.model.Dividend;
import com.single.project.model.ScrapedResult;
import com.single.project.model.constants.CacheKey;
import com.single.project.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Locale;
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

    public List<Company> getAllCompany() {

        return companyRepository.findAll().stream()
                .map(e -> Company.builder()
                        .ticker(e.getTicker())
                        .name(e.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public String deleteCompany(String ticker) {
        ticker = ticker.toUpperCase(Locale.ROOT);
        CompanyEntity company = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사 입니다."));

        dividendRepository.deleteAllByCompanyId(company.getId());
        companyRepository.delete(company);

        return company.getName();
    }

    @Cacheable(key = "#ticker", value= CacheKey.KEY_FINANCE)
    public ScrapedResult getCompany(String ticker) {
        ticker = ticker.toUpperCase(Locale.ROOT);
        CompanyEntity companyEntity = companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사 입니다. "));

        List<DividendEntity> dividendEntity = dividendRepository.findByCompanyId(companyEntity.getId());

        Company company = Company.builder()
                .name(companyEntity.getName())
                .ticker(companyEntity.getTicker())
                .build();

        List<Dividend> dividends = dividendEntity.stream()
                .map(e -> Dividend.builder()
                        .date(e.getDate())
                        .dividend(e.getDividend())
                        .build())
                .collect(Collectors.toList());

        return ScrapedResult.builder()
                .company(company)
                .dividends(dividends)
                .build();
    }

    public List<String> getCompanyNameByKeyword(String keyword) {

        List<CompanyEntity> companyEntityList = companyRepository.findByNameStartingWithIgnoreCase(keyword);

        return companyEntityList.stream()
                .map(e -> e.getName())
                .collect(Collectors.toList());
    }
}
