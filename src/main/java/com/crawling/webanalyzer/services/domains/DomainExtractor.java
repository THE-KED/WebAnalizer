package com.crawling.webanalyzer.services.domains;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DomainExtractor {

    public static String extract(String url) {

        // Définition de l'expression régulière
        String regex = "(?<=://)[^/]+";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        try {
            //recherche de la sous chaine correspondante
            if(matcher.find()){
                // extraction du premier et second niveau de domaine
                List<String> parts = new ArrayList<>(Arrays.asList(matcher.group().split("\\.")));
                String firstLevelDomain = parts.removeLast();
                String secondLevelDomain = parts.removeLast();
                return secondLevelDomain+"."+firstLevelDomain;
            }
        }catch (Throwable e){
            return "unknown";
        }
        return "unknown";
    }
}
