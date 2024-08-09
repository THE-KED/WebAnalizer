package com.crawling.webanalyzer.services.scrapper;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ExtractorTest {


    @BeforeEach
    public void setUp() {
        // Reset the extracted document before each test
        Extractor.getDocument(); // You may want to add a reset method in Extractor to clear extracted
    }

    @Test
    public void testExtract() throws IOException {
        // Mocking Jsoup.connect().get() to return a predefined Document
        String url = "https://docs.oracle.com/en/";

        // Call the method to test
        Document result = Extractor.Extract(url);

        // Assertions
        assertNotNull(result, "Document should not be null");
        assertEquals("Oracle Help Center", result.title(), "Oracle Help Center");
    }

    @Test
    public void testExtractThrows() {
        String invalidUrl = "invalid-url";

        assertThrows(IllegalArgumentException.class, () -> {
            Extractor.Extract(invalidUrl);
        }, "Expected Extract() to throw Don't Show Toolbar, but it didn't");

        String unRechableUrl = "https://mykedschool.000webhostapp.com";

        assertThrows(IOException.class, () -> {
            Extractor.Extract(unRechableUrl);
        }, "Expected Extract() to throw IOException, but it didn't");
    }

}
