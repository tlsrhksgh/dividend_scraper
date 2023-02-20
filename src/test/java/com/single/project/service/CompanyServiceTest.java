package com.single.project.service;

import com.single.project.domain.company.CompanyEntity;
import com.single.project.domain.company.CompanyRepository;
import com.single.project.domain.dividend.DividendEntity;
import com.single.project.domain.dividend.DividendRepository;
import com.single.project.model.Company;
import com.single.project.model.Dividend;
import com.single.project.model.ScrapedResult;
import com.single.project.scraper.YahooFinanceScraper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DividendRepository dividendRepository;

    @Mock
    private YahooFinanceScraper yahooFinanceScraper;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void 모든회사_name_ticker_조회() {
        //given
        List<CompanyEntity> companyEntityList = Arrays.asList(
                CompanyEntity.builder()
                        .ticker("t1")
                        .name("test1")
                        .build(),
                CompanyEntity.builder()
                        .ticker("t2")
                        .name("test2")
                        .build(),
                CompanyEntity.builder()
                        .ticker("t3")
                        .name("test3")
                        .build());
        given(companyRepository.findAll())
                .willReturn(companyEntityList);

        //when
        Company company = companyService.getAllCompany().get(0);

        //then
        assertEquals("t1", company.getTicker());
        assertEquals("test1", company.getName());
    }

    @Test
    void 회사_배당금_정보_삭제() {
        //given
        CompanyEntity companyEntity = CompanyEntity.builder()
                .id(1L)
                .name("test")
                .ticker("t1")
                .build();

        ArgumentCaptor<CompanyEntity> companyCaptor = ArgumentCaptor.forClass(CompanyEntity.class);

        //when
        dividendRepository.deleteAllByCompanyId(companyEntity.getId());
        companyRepository.delete(companyEntity);

        //then
        verify(dividendRepository, times(1)).deleteAllByCompanyId(companyEntity.getId());
        verify(companyRepository, times(1)).delete(companyCaptor.capture());
        assertEquals("test", companyCaptor.getValue().getName());
        assertEquals("t1", companyCaptor.getValue().getTicker());
    }

    @Test
    void 하나의_회사만_조회() {
        //given
        CompanyEntity companyEntity = CompanyEntity.builder()
                .id(1L)
                .ticker("t1")
                .name("test")
                .build();
        List<DividendEntity> dividendEntity = Arrays.asList(new DividendEntity(companyEntity.getId(), Dividend.builder()
                .dividend("0.25")
                .date(LocalDateTime.now())
                .build()));
        given(companyRepository.findByTicker(anyString()))
                .willReturn(Optional.of(companyEntity));
        given(dividendRepository.findByCompanyId(anyLong()))
                .willReturn(dividendEntity);

        //when
        ScrapedResult scrapResult = companyService.getCompany("t1");

        //then
        assertEquals(scrapResult.getCompany().getName(), "test");
        assertEquals(scrapResult.getCompany().getTicker(), "t1");
        assertEquals(scrapResult.getDividends().get(0).getDividend(), "0.25");
    }

    @Test
    void 키워드로_회사_조회() {
        //given
        List<CompanyEntity> companyEntityList = Arrays.asList(CompanyEntity.builder()
                        .name("test")
                        .ticker("t1")
                        .build());
        given(companyRepository.findByNameStartingWithIgnoreCase(anyString()))
                .willReturn(companyEntityList);

        //when
        List<String> companies = companyService.getCompanyNameByKeyword("t");

        //then
        assertEquals(companies.get(0), "test");
    }

}