package com.crawling.webanalyzer.services.scrapper;

import org.jsoup.nodes.Document;

public class TitleDetector{

    public static String detect(Document document) {
        return document.title();
    }
}
