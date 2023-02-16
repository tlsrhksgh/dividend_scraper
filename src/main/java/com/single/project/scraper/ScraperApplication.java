package com.single.project.scraper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

//@SpringBootApplication
public class ScraperApplication {

    public static void main(String[] args) {

//        SpringApplication.run(ScraperApplication.class, args);

        try {
            String url = "https://finance.yahoo.com/quote/COKE/history?period1=187920000&period2=1676505600&interval=capitalGain%7Cdiv%7Csplit&filter=div&frequency=1mo";
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");
            Element tableElement = elements.get(0);

            Element tbody = tableElement.children().get(1);

            for (Element e : tbody.children()) {
                String txt = e.text();
                System.out.println(txt);
                if(!txt.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = txt.split(" ");
                String month = splits[0];
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
