package com.crawling.webanalyzer.services.scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class RubriqueDetector{

    //Detecte et conte le nombre HTML Rubriques (h1 to h6).
    public static Map<String,Integer> detect(Document document) {
        Elements elements;
        Map<String, Integer> rubrique = new HashMap<>();
        for (int i = 1; i<=6; i++) {
            elements = document.select("h"+i);
            rubrique.put("h"+i,elements.size());
        }

        return rubrique;
    }
}
