package com.crawling.webanalyzer.services.scrapper;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;

@Slf4j
public class AuthFormDetector{


    public static boolean detect(Document document) {
        Elements elements = document.select("form");

        for(Element element:elements) {
            Elements el = element.children();
            for (Element child:el) {
                if(!child.select("input[type=password]").isEmpty()) {
                        return true;
                }
            }
        }

        return false;
    }
}
