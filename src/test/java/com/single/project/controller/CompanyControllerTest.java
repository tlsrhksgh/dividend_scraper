package com.single.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.single.project.model.Company;
import com.single.project.model.Dividend;
import com.single.project.model.ScrapedResult;
import com.single.project.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {
    @MockBean
    private CompanyService companyService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 전체회사_조회() throws Exception {
        //given
        List<Company> companyList = Arrays.asList(
                Company.builder()
                        .name("test1")
                        .ticker("t1")
                        .build(),
                Company.builder()
                        .name("test2")
                        .ticker("t2")
                        .build(),
                Company.builder()
                        .name("test3")
                        .ticker("t3")
                        .build());
        given(companyService.getAllCompany())
                .willReturn(companyList);

        //when
        //then
        mockMvc.perform(get("/company"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[0].ticker").value("t1"))
                .andExpect(jsonPath("$[1].name").value("test2"))
                .andExpect(jsonPath("$[1].ticker").value("t2"))
                .andExpect(jsonPath("$[2].name").value("test3"))
                .andExpect(jsonPath("$[2].ticker").value("t3"));
    }

    @Test
    void 하나의회사에대한_배당금_회사이름_조회() throws Exception {
        //given
        Company company = Company.builder()
                .name("test1")
                .ticker("t1")
                .build();

        List<Dividend> dividends = Arrays.asList(
                Dividend.builder()
                        .date(LocalDateTime.now())
                        .dividend("0.25")
                        .build(),
                Dividend.builder()
                        .date(LocalDateTime.now())
                        .dividend("0.26")
                        .build(),
                Dividend.builder()
                        .date(LocalDateTime.now())
                        .dividend("0.27")
                        .build());
        given(companyService.getCompany(anyString()))
                .willReturn(ScrapedResult.builder()
                        .company(company)
                        .dividends(dividends)
                        .build());

        //when
        //then
        mockMvc.perform(get("/company/t1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company.name").value("test1"))
                .andExpect(jsonPath("$.company.ticker").value("t1"))
                .andExpect(jsonPath("$.dividends[0].dividend").value("0.25"))
                .andExpect(jsonPath("$.dividends[1].dividend").value("0.26"))
                .andExpect(jsonPath("$.dividends[2].dividend").value("0.27"));
    }

    @Test
    void 회사_등록() throws Exception {
        //given
        given(companyService.save(anyString()))
                .willReturn(Company.builder()
                        .name("test")
                        .ticker("t1")
                        .build());

        //when
        //then
        mockMvc.perform(post("/company")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Company.builder()
                                        .name("testasd")
                                        .ticker("twea2")
                                        .build()
                        )))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.ticker").value("t1"));
    }

    @Test
    void 회사_이름_배당금_삭제() throws Exception {
        //given
        given(companyService.deleteCompany(anyString()))
                .willReturn("test");

        //when
        //then
        mockMvc.perform(delete("/company/test"))
                .andDo(print())
                .andExpect(content().string("test"));
    }

    @Test
    void 자동완성_기능_조회() throws Exception {
        //given
        given(companyService.getCompanyNameByKeyword(anyString()))
                .willReturn(Arrays.asList("test", "test1", "test2"));

        //when
        //then
        mockMvc.perform(get("/company/autocomplete?keyword=t"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value("test"))
                .andExpect(jsonPath("$.[1]").value("test1"))
                .andExpect(jsonPath("$.[2]").value("test2"));
    }

}