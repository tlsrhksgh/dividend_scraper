package com.single.project.scraper;

import com.single.project.model.Company;
import com.single.project.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
