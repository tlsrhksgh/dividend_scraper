package com.single.project.controller;

import com.single.project.model.Company;
import com.single.project.model.ScrapedResult;
import com.single.project.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/company")
@AllArgsConstructor
@RestController
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<List<Company>> searchAllCompany() {
        List<Company> companyList = companyService.getAllCompany();

        return ResponseEntity.ok(companyList);
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<ScrapedResult> searchCompanyAndDividend(@PathVariable String ticker) {
        ScrapedResult scrapedResult = companyService.getCompany(ticker);

        return ResponseEntity.ok(scrapedResult);
    }

    @PostMapping
    public ResponseEntity<Company> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)) {
           throw new RuntimeException("입력하신 티커를 확인할 수 없습니다.");
        }

        Company company = companyService.save(request.getTicker());

        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/{ticker}")
    public ResponseEntity<String> deleteCompany(@PathVariable String ticker) {
        String companyName = companyService.deleteCompany(ticker);

        return ResponseEntity.ok(companyName);
    }

}
