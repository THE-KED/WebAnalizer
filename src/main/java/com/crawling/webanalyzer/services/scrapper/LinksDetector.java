package com.crawling.webanalyzer.services.scrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class LinksDetector{
    private static final Logger log = LoggerFactory.getLogger(LinksDetector.class);
    public String Domaine;

    public LinksDetector(String domaine) {
        Domaine = domaine;
    }

    public static List<String>[] detect(Document document ,String Domaine) {
        Elements elements = document.select("a");
        //links[0]= liste de liens internes et links[1]= liste de liens externes
        ArrayList[] links = new ArrayList[2];

        // Initialisation de chaque éléments du tableau
        links[0] = new ArrayList<>();
        links[1] = new ArrayList<>();

        for (Element element : elements){
            try {
//                log.info(element.absUrl("href"));
                URI href = new URI(element.absUrl("href"));
                if(href.getAuthority()!= null && href.getAuthority().equals(Domaine))
                    links[0].add(element.absUrl("href"));
                else
                    links[1].add(element.absUrl("href"));
            } catch (URISyntaxException e) {
                log.error(e.getMessage());
            }
        }
        return links;
    }
}
