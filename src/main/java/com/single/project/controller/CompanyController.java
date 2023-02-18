package com.single.project.controller;

import com.single.project.model.Company;
import com.single.project.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/company")
@AllArgsConstructor
@RestController
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<?> searchCompany() {
        return null;
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

    @DeleteMapping
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        return null;
    }

}
