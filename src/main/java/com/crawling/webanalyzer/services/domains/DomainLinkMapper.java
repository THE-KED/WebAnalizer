package com.crawling.webanalyzer.services.domains;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DomainLinkMapper {

    public static Map<String, List<String>> map(List<String> links){

        Map<String, List<String>> domainToLinksMap = new HashMap<>();
        domainToLinksMap = links.stream().collect(Collectors.groupingBy(DomainExtractor::extract));

        return domainToLinksMap;
    }
}
