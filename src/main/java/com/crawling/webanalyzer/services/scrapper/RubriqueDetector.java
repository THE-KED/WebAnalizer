package com.crawling.webanalyzer.services.scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class RubriqueDetector{

    //Detecte et conte le nombre HTML Rubriques (h1 to h6).
    public static int[][] detect(Document document) {
        int[][] rubriqueCount = new int[6][1];
        Elements elements = null;
        for (int i = 1; i<=6; i++) {
            elements = document.select("h"+i);
            rubriqueCount[i-1][0]=(elements.size());
        }

        return rubriqueCount;
    }
}
