package com.crawling.webanalyzer.services.scrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Extractor {

    private static Document extracted;

    public static Document Extract(String url) throws IOException {
        extracted = Jsoup.connect(url).get();
        return extracted;
    }
    public static Document getDocument(){
        return extracted;
    }

}
