package com.crawling.webanalyzer.services.domains;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DomainExtractorTest {

    @Test
    public void shouldExtractDomainFromUrl() {
        String url = "https://www.example.com/articles/web-scraping";
        String domain = DomainExtractor.extract(url);
        assertEquals("example.com", domain);
    }
}
