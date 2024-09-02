package com.crawling.webanalyzer.models;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PageInfos {

    private String htmlVersion;
    private String pageTitle;
    private Map<String,Integer> rubriquesNumber;
    private List<String> links;
    private Map<String,List<String>> linksByDomain;
    private boolean hadAuthForm;
}
