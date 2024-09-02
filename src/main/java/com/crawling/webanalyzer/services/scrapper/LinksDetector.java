package com.crawling.webanalyzer.services.scrapper;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.net.URI;
import java.net.URL;
import java.util.List;

@Slf4j
public class LinksDetector{

    public static List<String> detect(Document document) {
        Elements elements = document.select("a");
        List<String> links;

       links = elements.stream()
               .map(elt->elt.absUrl("href"))
               .distinct()
               .filter(LinksDetector::isValidURL)
               .toList();

        return links;
    }

    public static boolean isValidURL(String href) {
        try {
            URI uri = new URI(href);
            URL url = uri.toURL();
            return true;

        } catch (Exception e) {
            log.warn("{}, href = {}",e.getMessage(),href);
            return false;
        }
    }


}
